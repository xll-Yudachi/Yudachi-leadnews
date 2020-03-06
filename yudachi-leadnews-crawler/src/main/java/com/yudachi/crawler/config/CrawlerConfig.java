package com.yudachi.crawler.config;

import com.yudachi.crawler.helper.CookieHelper;
import com.yudachi.crawler.helper.CrawlerHelper;
import com.yudachi.crawler.process.entity.CrawlerConfigProperty;
import com.yudachi.crawler.process.scheduler.DbAndRedisScheduler;
import com.yudachi.crawler.service.CrawlerIpPoolService;
import com.yudachi.crawler.utils.SeleniumClient;
import com.yudachi.model.crawler.core.callback.DataValidateCallBack;
import com.yudachi.model.crawler.core.callback.ProxyProviderCallBack;
import com.yudachi.model.crawler.core.parse.ParseRule;
import com.yudachi.model.crawler.core.proxy.CrawlerProxy;
import com.yudachi.model.crawler.core.proxy.CrawlerProxyProvider;
import com.yudachi.model.crawler.enums.CrawlerEnum;
import com.yudachi.model.crawler.pojos.ClIpPool;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Spider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Configuration
@Log4j2
@Data
@PropertySource("classpath:crawler.properties")
@ConfigurationProperties(prefix = "crawler.init.url")
public class CrawlerConfig {

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("crawler");
    private static final String CRUX_COOKIE_NAME = resourceBundle.getString("crux.cookie.name");
    private boolean isUsedProxyIp = Boolean.parseBoolean(resourceBundle.getString("proxy.isUsedProxyIp"));
    private String prefix;
    private String suffix;

    /**
     * 是否开启帮助页面分页抓取
     */
    private Integer crawlerHelpNextPagingSize = Integer.parseInt(resourceBundle.getString("crawler.help.nextPagingSize"));
    /**
     * 帮助页面抓取Xpath
     */
    private String helpCrawlerXpath = "//div[@class='article-list']/div[@class='article-item-box']/h4/a";

    /**
     * 初始化抓取的Xpath
     */
    private String initCrawlerXpath = "//ul[@class='feedlist_mod']/li[@class='clearfix']/div[@class='list_con']/dl[@class='list_userbar']/dd[@class='name']/a";


    private Spider spider;

    /**
     * 拼接初始化的URL
     *
     * @return
     */
    public List<String> getInitCrawlerUrlList() {
        List<String> initCrawlerUrlList = null;
        if (StringUtils.isNotEmpty(suffix)) {
            String[] initCrawlerUrlArray = suffix.split(",");
            if (null != initCrawlerUrlArray && initCrawlerUrlArray.length > 0) {
                for (int i = 0; i < initCrawlerUrlArray.length; i++) {
                    String initUrl = initCrawlerUrlArray[i];
                    if (StringUtils.isNotEmpty(initUrl)) {
                        if (!initUrl.toLowerCase().startsWith("http")) {
                            initUrl = prefix + initUrl;
                            initCrawlerUrlArray[i] = initUrl;
                        }
                    }
                }
            }
            initCrawlerUrlList = Arrays.asList(initCrawlerUrlArray).stream().filter(x -> StringUtils.isNotEmpty(x)).collect(Collectors.toList());
        }
        return initCrawlerUrlList;
    }

    @Bean
    public SeleniumClient getSeleniumClient() {
        return new SeleniumClient();
    }

    /**
     * 设置Cookie辅助类
     *
     * @return
     */
    @Bean
    public CookieHelper getCookieHelper() {
        return new CookieHelper(CRUX_COOKIE_NAME);
    }

    /**
     * 数据校验匿名内部类
     *
     * @param cookieHelper
     * @return
     */
    private DataValidateCallBack getDataValidateCallBack(CookieHelper cookieHelper) {
        return new DataValidateCallBack() {
            @Override
            public boolean validate(String content) {
                boolean flag = true;
                if (StringUtils.isEmpty(content)) {
                    flag = false;
                } else {
                    boolean isContains_acw_sc_v2 = content.contains("acw_sc__v2");
                    boolean isContains_location_reload = content.contains("document.location.reload()");
                    if (isContains_acw_sc_v2 && isContains_location_reload) {
                        flag = false;
                    }
                }
                return flag;
            }
        };
    }

    /**
     * CrawerHelper 辅助类
     *
     * @return
     */
    @Bean
    public CrawlerHelper getCrawerHelper() {
        CookieHelper cookieHelper = getCookieHelper();
        CrawlerHelper crawerHelper = new CrawlerHelper();
        DataValidateCallBack dataValidateCallBack = getDataValidateCallBack(cookieHelper);
        crawerHelper.setDataValidateCallBack(dataValidateCallBack);
        return crawerHelper;
    }

    @Bean
    public CrawlerProxyProvider getCrawlerProxyProvider() {
        CrawlerProxyProvider crawlerProxyProvider = new CrawlerProxyProvider();
        crawlerProxyProvider.setUsedProxyIp(isUsedProxyIp);
        // 设置动态代理
        crawlerProxyProvider.setProxyProviderCallBack(new ProxyProviderCallBack(){
            @Override
            public List<CrawlerProxy> getProxyList() {
                return getCrawlerProxyList();
            }

            @Override
            public void unavilable(CrawlerProxy crawlerProxy) {
                unavilableProxy(crawlerProxy);
            }
        });
        return crawlerProxyProvider;
    }

    // 代理ip不可用处理
    private void unavilableProxy(CrawlerProxy crawlerProxy) {
        if (crawlerProxy != null){
            crawlerIpPoolService.unvailableProxy(crawlerProxy, "自动禁用");
        }
    }

    @Autowired
    private CrawlerIpPoolService crawlerIpPoolService;

    // 获取初始化ip列表
    private List<CrawlerProxy> getCrawlerProxyList() {
        List<CrawlerProxy> crawlerProxyList = new ArrayList<>();
        ClIpPool clIpPool = new ClIpPool();
        clIpPool.setDuration(5);
        List<ClIpPool> clIpPools = crawlerIpPoolService.queryAvailableList(clIpPool);
        if (null != clIpPools && !clIpPools.isEmpty()){
            for (ClIpPool ipPool : clIpPools) {
                crawlerProxyList.add(new CrawlerProxy(ipPool.getIp(), ipPool.getPort()));
            }
        }

        return crawlerProxyList;
    }

    @Bean
    public CrawlerConfigProperty getCrawlerConfigProperty() {
        CrawlerConfigProperty crawlerConfigProperty = new CrawlerConfigProperty();
        crawlerConfigProperty.setInitCrawlerUrlList(getInitCrawlerUrlList());
        crawlerConfigProperty.setHelpCrawlerXpath(helpCrawlerXpath);
        crawlerConfigProperty.setTargetParseRuleList(getTargetParseRuleList());
        crawlerConfigProperty.setCrawlerHelpNextPagingSize(crawlerHelpNextPagingSize);
        crawlerConfigProperty.setInitCrawlerXpath(initCrawlerXpath);
        return crawlerConfigProperty;
    }

    /**
     * 目标页面抓取规则
     *
     * @return
     */
    public List<ParseRule> getTargetParseRuleList() {
        List<ParseRule> parseRuleList = new ArrayList<ParseRule>() {{
            //标题
            add(new ParseRule("title", CrawlerEnum.ParseRuleType.XPATH, "//h1[@class='title-article']/text()"));
            //作者
            add(new ParseRule("author", CrawlerEnum.ParseRuleType.XPATH, "//a[@class='follow-nickName']/text()"));
            //发布日期
            add(new ParseRule("releaseDate", CrawlerEnum.ParseRuleType.XPATH, "//span[@class='time']/text()"));
            //标签
            add(new ParseRule("labels", CrawlerEnum.ParseRuleType.XPATH, "//span[@class='tags-box']/a/text()"));
            //个人空间
            add(new ParseRule("personalSpace", CrawlerEnum.ParseRuleType.XPATH, "//a[@class='follow-nickName']/@href"));
            //阅读量
            add(new ParseRule("readCount", CrawlerEnum.ParseRuleType.XPATH, "//span[@class='read-count']/text()"));
            //点赞量
            add(new ParseRule("likes", CrawlerEnum.ParseRuleType.XPATH, "//div[@class='tool-box']/ul[@class='meau-list']/li[@class='btn-like-box']/button/p/text()"));
            //回复次数
            add(new ParseRule("commentCount", CrawlerEnum.ParseRuleType.XPATH, "//div[@class='tool-box']/ul[@class='meau-list']/li[@class='to-commentBox']/button/p/text()"));
            //html内容
            add(new ParseRule("content", CrawlerEnum.ParseRuleType.XPATH, "//div[@id='content_views']/html()"));

        }};
        return parseRuleList;
    }

    @Value("${redis.host}")
    private String redisHost;
    @Value("${redis.port}")
    private int reidsPort;
    @Value("${redis.timeout}")
    private int reidstimeout;
    @Value("${redis.password}")
    private String reidsPassword;
    @Bean
    public DbAndRedisScheduler getDbAndRedisScheduler() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        JedisPool jedisPool = new JedisPool(genericObjectPoolConfig, redisHost, reidsPort, reidstimeout, null, 0);
        return new DbAndRedisScheduler(jedisPool);
    }
}
