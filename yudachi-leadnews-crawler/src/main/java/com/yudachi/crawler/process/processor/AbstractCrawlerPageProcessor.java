package com.yudachi.crawler.process.processor;

import com.yudachi.crawler.helper.CrawlerHelper;
import com.yudachi.crawler.process.AbstractProcessFlow;
import com.yudachi.crawler.process.CrawlerPageProcessorManager;
import com.yudachi.crawler.process.entity.ProcessFlowData;
import com.yudachi.crawler.utils.ParseRuleUtils;
import com.yudachi.model.crawler.core.parse.ParseItem;
import com.yudachi.model.crawler.core.parse.ParseRule;
import com.yudachi.model.crawler.core.parse.impl.CrawlerParseItem;
import com.yudachi.model.crawler.enums.CrawlerEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Log4j2
public abstract class AbstractCrawlerPageProcessor extends AbstractProcessFlow implements PageProcessor {

    @Autowired
    private CrawlerHelper crawlerHelper;

    @Override
    public void handle(ProcessFlowData processFlowData) {

    }

    @Override
    public CrawlerEnum.ComponentType getComponentType() {
        return CrawlerEnum.ComponentType.PAGEPROCESSOR;
    }

    @Resource
    private CrawlerPageProcessorManager crawlerPageProcessorManager;

    /**
     * process是定制爬虫逻辑的核心接口方法，在这里编写抽取逻辑
     * @param page
     */
    @Override
    public void process(Page page) {
        long currentTimeMillis = System.currentTimeMillis();
        String handelType = crawlerHelper.getHandelType(page.getRequest());
        log.info("开始解析数据页面：url:{},handelType:{}",page.getUrl(),handelType);
        crawlerPageProcessorManager.handel(page);
        log.info("解析数据页面完成，url:{},handelType:{},耗时:{}",page.getUrl(),handelType,System.currentTimeMillis()-currentTimeMillis);
    }

    @Override
    public Site getSite() {
        Site site = Site.me().setRetryTimes(getRetryTimes()).setRetrySleepTime(getRetrySleepTime()).setSleepTime(getSleepTime()).setTimeOut(getTimeout());
        //header  配置
        Map<String, String> headerMap = getHeaderMap();
        if(null != headerMap && !headerMap.isEmpty()){
            for(Map.Entry<String,String> entry:headerMap.entrySet()){
                site.addHeader(entry.getKey(),entry.getValue());
            }
        }

        return site;
    }

    /**
     * 解析url列表
     * @param helpParseRuleList
     * @return
     */
    public List<String> getHelpUrlList(List<ParseRule> helpParseRuleList){
        List<String> helpUrlList = new ArrayList<>();
        for(ParseRule parseRule:helpParseRuleList){
            List<String> urlLinks = ParseRuleUtils.getUrlLinks(parseRule.getParseContentList());
            helpUrlList.addAll(urlLinks);
        }
        return helpUrlList;
    }

    /**
     * 添加数据到爬虫列表
     * @param urlList
     * @param request
     * @param documentType
     */
    public void addSpiderRequest(List<String> urlList, Request request,CrawlerEnum.DocumentType documentType){
        List<ParseItem> parseItemList = new ArrayList<>();
        if(null!=urlList&& !urlList.isEmpty()){
            for (String url : urlList) {
                CrawlerParseItem crawlerParseItem = new CrawlerParseItem();
                crawlerParseItem.setUrl(url);
                crawlerParseItem.setHandelType(crawlerHelper.getHandelType(request));
                crawlerParseItem.setDocumentType(documentType.name());
                parseItemList.add(crawlerParseItem);
            }
        }
        addSpiderRequest(parseItemList);
    }

    /**
     * 处理页面
     * @param page
     */
    public abstract void handelPage(Page page);

    /**
     * 是否需要处理类型
     * @param handelType
     * @return
     */
    public abstract boolean isNeedHandelType(String handelType);

    /**
     * 是否需要文档类型
     * @param documentType
     * @return
     */
    public abstract boolean isNeedDocumentType(String documentType);



    /**
     * 重试次数
     * @return
     */
    public int getRetryTimes(){
        return 3;
    }

    /**
     * 重试间隔时间
     * @return
     */
    public int getRetrySleepTime(){
        return 1000;
    }

    /**
     * 抓取间隔时间
     * @return
     */
    public int getSleepTime(){
        return 1000;
    }

    /**
     * 超时时间
     * @return
     */
    public int getTimeout(){
        return 10000;
    }
}
