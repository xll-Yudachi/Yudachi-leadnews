package com.yudachi.crawler.process;

import com.yudachi.crawler.helper.CrawlerHelper;
import com.yudachi.crawler.process.processor.AbstractCrawlerPageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;

/**
 * 爬虫文档流程管理
 */
@Component
public class CrawlerPageProcessorManager {

    @Autowired
    private CrawlerHelper crawlerHelper;

    @Resource
    private List<AbstractCrawlerPageProcessor>  abstractCrawlerPageProcessorList;

    /**
     * 初始化注入接口顺序的方法
     */

    @PostConstruct
    public void initProcessingFlow(){
        if(abstractCrawlerPageProcessorList!=null && !abstractCrawlerPageProcessorList.isEmpty()){
            abstractCrawlerPageProcessorList.sort(new Comparator<ProcessFlow>() {
                @Override
                public int compare(ProcessFlow o1, ProcessFlow o2) {
                    if(o1.getPriority() > o2.getPriority()){
                        return 1;
                    }else if(o1.getPriority() > o2.getPriority()){
                        return -1;
                    }
                    return 0;
                }
            });
        }
    }

    /**
     * 处理数据
     * @param page
     */
    public void handel(Page page){
        String handelType = crawlerHelper.getHandelType(page.getRequest());
        String documentType = crawlerHelper.getDocumentType(page.getRequest());
        for (AbstractCrawlerPageProcessor abstractCrawlerPageProcessor : abstractCrawlerPageProcessorList) {
            boolean needHandelType = abstractCrawlerPageProcessor.isNeedHandelType(handelType);
            boolean needDocumentType = abstractCrawlerPageProcessor.isNeedDocumentType(documentType);
            if(needHandelType && needDocumentType ){
                abstractCrawlerPageProcessor.handelPage(page);
            }
        }
    }

}
