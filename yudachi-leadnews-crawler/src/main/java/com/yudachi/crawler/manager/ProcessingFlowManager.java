package com.yudachi.crawler.manager;

import com.yudachi.crawler.config.CrawlerConfig;
import com.yudachi.crawler.process.ProcessFlow;
import com.yudachi.crawler.process.entity.CrawlerComponent;
import com.yudachi.crawler.process.entity.ProcessFlowData;
import com.yudachi.crawler.service.CrawlerNewsAdditionalService;
import com.yudachi.model.crawler.core.parse.ParseItem;
import com.yudachi.model.crawler.enums.CrawlerEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.Scheduler;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 前置数据处理
 * 对ProcessFlow 接口类型的类进行前置实例化做一些前置处理
 * 例如AbstractOriginalDataProcess 类的 handel 方式 初始化URL 以及初始化 代理数据
 * 并生成Spider 并自定启动
 * 是爬虫服务的入口
 */
@Component
@Log4j2
@SuppressWarnings("all")
public class ProcessingFlowManager {

    @Autowired
    private CrawlerConfig crawlerConfig;

    /**
     * 注入实现ProcessFlow 接口的所有类
     */
    @Resource
    private List<ProcessFlow> processFlowList;

    @Autowired
    private CrawlerNewsAdditionalService crawlerNewsAdditionalService;

    /**
     * spring 启动的时候就会进行调用
     * 对实现ProcessFlow接口的类根据getPriority() 接口对实现类进行从小到大的排序
     * 实现有序的责任链模式 一个模块处理一件事然后将数据传递到下个模块交给下各模块进行处理
     */
    @PostConstruct
    private void initProcessingFlow() {
        if (null != processFlowList && !processFlowList.isEmpty()) {
            processFlowList.sort(new Comparator<ProcessFlow>() {
                public int compare(ProcessFlow p1, ProcessFlow p2) {
                    if (p1.getPriority() > p2.getPriority()) {
                        return 1;
                    } else if (p1.getPriority() < p2.getPriority()) {
                        return -1;
                    }
                    return 0;
                }
            });
        }
        Spider spider = configSpider();
        crawlerConfig.setSpider(spider);
    }


    /**
     * 抓取组件封装
     * <p>
     * 根据接口 CrawlerEnum.ComponentType getComponentType()获取的CrawlerEnum.ComponentType 类封装组件CrawlerComponent
     *
     * @param processFlowList
     * @return
     */
    private CrawlerComponent getComponent(List<ProcessFlow> processFlowList) {
        CrawlerComponent component = new CrawlerComponent();
        for (ProcessFlow processingFlow : processFlowList) {
            if (processingFlow.getComponentType() == CrawlerEnum.ComponentType.PAGEPROCESSOR) {
                component.setPageProcessor((PageProcessor) processingFlow);
            } else if (processingFlow.getComponentType() == CrawlerEnum.ComponentType.PIPELINE) {
                component.addPipeline((Pipeline) processingFlow);
            } else if (processingFlow.getComponentType() == CrawlerEnum.ComponentType.SCHEDULER) {
                component.setScheduler((Scheduler) processingFlow);
            } else if (processingFlow.getComponentType() == CrawlerEnum.ComponentType.DOWNLOAD) {
                component.setDownloader((Downloader) processingFlow);
            }
        }
        return component;
    }


    private Spider configSpider() {
        Spider spider = initSpider();
        spider.thread(5);
        return spider;
    }

    /**
     * 根据ProcessFlow接口getComponentType() 接口类型数生成Spider
     *
     * @param
     * @return
     */
    private Spider initSpider() {
        Spider spider = null;
        CrawlerComponent component = getComponent(processFlowList);
        if (null != component) {
            PageProcessor pageProcessor = component.getPageProcessor();
            if (null != pageProcessor) {
                spider = Spider.create(pageProcessor);
            }
            if (null != spider && null != component.getScheduler()) {
                spider.setScheduler(component.getScheduler());
            }
            if (null != spider && null != component.getDownloader()) {
                spider.setDownloader(component.getDownloader());
            }
            List<Pipeline> pipelineList = component.getPipelineList();
            if (null != spider && null != pipelineList && !pipelineList.isEmpty()) {
                for (Pipeline pipeline : pipelineList) {
                    spider.addPipeline(pipeline);
                }
            }
        }
        return spider;
    }


    /**
     * 正向处理
     */
    public void handle() {
        startTask(null, CrawlerEnum.HandelType.FORWARD);
    }

    /**
     * 逆向处理
     */
    public void reverseHandel() {
        List<ParseItem> parseItemList = crawlerNewsAdditionalService.queryIncrementParseItem(new Date());
        handleReverseData(parseItemList);
        log.info("开始进行数据你想更新，增量数据数量为：{}", parseItemList.size());
        if (null != parseItemList && !parseItemList.isEmpty()){
            startTask(parseItemList, CrawlerEnum.HandelType.REVERSE);
        }else{
            log.info("增量数据为空不能进行数据更新");
        }
    }

    /**
     * 反向同步数据处理
     *
     * @param parseItemList
     */
    public void handleReverseData(List<ParseItem> parseItemList) {
        if (parseItemList != null && !parseItemList.isEmpty()){
            for (ParseItem parseItem : parseItemList) {
                parseItem.setDocumentType(CrawlerEnum.DocumentType.PAGE.name());
                parseItem.setHandelType(CrawlerEnum.HandelType.REVERSE.name());
            }
        }
    }


    /**
     * 开始处理爬虫任务
     *
     * @param parseItemList 处理初始化URL列表
     * @param handelType    FORWARD 正向处理 REVERSE 逆向处理
     */
    public void startTask(List<ParseItem> parseItemList, CrawlerEnum.HandelType handelType) {
        ProcessFlowData processFlowData = new ProcessFlowData();
        processFlowData.setHandelType(handelType);
        processFlowData.setParseItemList(parseItemList);
        for (ProcessFlow processingFlow : processFlowList) {
            processingFlow.handle(processFlowData);
        }
        crawlerConfig.getSpider().start();
    }
}
