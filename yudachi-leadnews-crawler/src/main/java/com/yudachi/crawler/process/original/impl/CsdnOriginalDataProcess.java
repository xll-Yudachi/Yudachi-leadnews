package com.yudachi.crawler.process.original.impl;

import com.yudachi.crawler.config.CrawlerConfig;
import com.yudachi.crawler.process.entity.ProcessFlowData;
import com.yudachi.crawler.process.original.AbstractOriginalDataProcess;
import com.yudachi.model.crawler.core.parse.ParseItem;
import com.yudachi.model.crawler.core.parse.impl.CrawlerParseItem;
import com.yudachi.model.crawler.enums.CrawlerEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
public class CsdnOriginalDataProcess extends AbstractOriginalDataProcess {

    @Autowired
    private CrawlerConfig crawlerConfig;

    @Override
    public List<ParseItem> parseOriginalRequestData(ProcessFlowData processFlowData) {
        List<ParseItem> parseItemList = null;
        //从crawlerConfigProperty 中获取初始化URL列表
        List<String> initCrawlerUrlList = crawlerConfig.getInitCrawlerUrlList();
        if (null != initCrawlerUrlList && !initCrawlerUrlList.isEmpty()) {
            parseItemList = initCrawlerUrlList.stream().map(url -> {
                CrawlerParseItem parseItem = new CrawlerParseItem();
                url = url + "?rnd=" + System.currentTimeMillis();
                parseItem.setUrl(url);
                parseItem.setDocumentType(CrawlerEnum.DocumentType.INIT.name());
                parseItem.setHandelType(processFlowData.getHandelType().name());
                log.info("初始化URL:{}", url);
                return parseItem;
            }).collect(Collectors.toList());
        }
        return parseItemList;
    }

    //优先级
    @Override
    public int getPriority() {
        return 10;
    }
}
