package com.yudachi.crawler.process.processor.impl;

import com.yudachi.crawler.helper.CrawlerHelper;
import com.yudachi.crawler.process.entity.CrawlerConfigProperty;
import com.yudachi.crawler.process.processor.AbstractCrawlerPageProcessor;
import com.yudachi.model.crawler.enums.CrawlerEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.List;

/**
 * 抓取帮助页面
 */
@Component
@Log4j2
public class CrawlerHelpPageProcessor extends AbstractCrawlerPageProcessor {

    /**
     * 帮助页面的后缀
     */
    private final String helpUrlSuffix = "?utm_source=feed";
    /**
     * 帮助页面分页后缀
     */
    private final String helpPagePagingSuffix = "/article/list/";

    @Autowired
    private CrawlerConfigProperty crawlerConfigProperty;

    @Autowired
    private CrawlerHelper crawlerHelper;

    /**
     * 处理数据
     * @param page
     */
    @Override
    public void handelPage(Page page) {
        String handelType = crawlerHelper.getHandelType(page.getRequest());
        long currentTime = System.currentTimeMillis();
        String requestUrl = page.getUrl().get();
        log.info("开始解析帮助页数据，url:{},handelType：{}", requestUrl, handelType);

        //获取配置的抓取规则
        String helpCrawlerXpath = crawlerConfigProperty.getHelpCrawlerXpath();

        List<String> helpUrlList = page.getHtml().xpath(helpCrawlerXpath).links().all();
        Integer crawlerHelpNextPagingSize = crawlerConfigProperty.getCrawlerHelpNextPagingSize();
        if (null != crawlerHelpNextPagingSize && crawlerHelpNextPagingSize > 1) {
            List<String> docPagePagingUrlList = getDocPagePagingUrlList(requestUrl, crawlerHelpNextPagingSize);
            if (null != docPagePagingUrlList && !docPagePagingUrlList.isEmpty()) {
                helpUrlList.addAll(docPagePagingUrlList);
            }
        }
        addSpiderRequest(helpUrlList, page.getRequest(), CrawlerEnum.DocumentType.PAGE);
        log.info("解析帮助页数据完成，url:{},handelType:{},耗时：{}", page.getUrl(), handelType, System.currentTimeMillis() - currentTime);
    }

    /**
     * 获取分页后的数据
     * @param url 处理的URL
     * @param pageSize 分页页数
     * @return
     */
    private List<String> getDocPagePagingUrlList(String url, int pageSize) {
        List<String> docPagePagingUrlList = null;
        if (url.endsWith(helpUrlSuffix)) {
            List<String> pagePagingUrlList = generateHelpPagingUrl(url, pageSize);
            docPagePagingUrlList = getHelpPagingDocUrl(pagePagingUrlList);
        }
        return docPagePagingUrlList;
    }

    /**
     * 生成分页URL
     * @param url 初始URL
     * @param pageSize 分页页数
     * @return
     */
    public List<String> generateHelpPagingUrl(String url, int pageSize) {
        String pageUrl = url.replace(helpUrlSuffix, helpPagePagingSuffix);
        List<String> pagePagingUrlList = new ArrayList<>();
        for (int i = 2; i <= pageSize; i++) {
            pagePagingUrlList.add(pageUrl + i);
        }
        return pagePagingUrlList;
    }

    /**
     * 获取分页后获取的URL
     * @param pagePagingUrlList
     * @return
     */
    public List<String> getHelpPagingDocUrl(List<String> pagePagingUrlList) {
        long currentTime = System.currentTimeMillis();
        log.info("开始进行分页抓取Doc页面");
        List<String> docUrlList = new ArrayList<>();
        int finalCount = 0;
        if (!pagePagingUrlList.isEmpty()) {
            for (String url : pagePagingUrlList) {
                log.info("开始进行Help页面分页处理，url:{}", url);
                String htmlData = getOriginalRequestHtmlData(url, null);
                boolean isValidate = crawlerHelper.getDataValidateCallBack().validate(htmlData);
                if (isValidate) {
                    List<String> urlList = new Html(htmlData).xpath(crawlerConfigProperty.getHelpCrawlerXpath()).links().all();
                    if (!urlList.isEmpty()) {
                        docUrlList.addAll(urlList);
                    } else {
                        finalCount++;
                        if (finalCount > 2) {
                            break;
                        }
                    }

                }
            }
        }
        log.info("分抓取Doc页面完成，耗时:{}", System.currentTimeMillis() - currentTime);
        return docUrlList;
    }

    /**
     * 处理的爬取类型
     * 只处理正向爬取
     * @param handelType
     * @return
     */
    @Override
    public boolean isNeedHandelType(String handelType) {
        return CrawlerEnum.HandelType.FORWARD.name().equals(handelType);
    }

    /**
     * 处理的文档类型
     * 只处理帮助页面
     * @param documentType
     * @return
     */
    @Override
    public boolean isNeedDocumentType(String documentType) {
        return CrawlerEnum.DocumentType.HELP.name().equals(documentType);
    }

    @Override
    public int getPriority() {
        return 110;
    }
}