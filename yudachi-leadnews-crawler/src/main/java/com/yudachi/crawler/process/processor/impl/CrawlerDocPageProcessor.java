package com.yudachi.crawler.process.processor.impl;

import com.yudachi.crawler.helper.CrawlerHelper;
import com.yudachi.crawler.process.entity.CrawlerConfigProperty;
import com.yudachi.crawler.process.processor.AbstractCrawlerPageProcessor;
import com.yudachi.crawler.utils.ParseRuleUtils;
import com.yudachi.model.crawler.core.parse.ParseRule;
import com.yudachi.model.crawler.enums.CrawlerEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;

import java.util.List;

/**
 * 文档页面抓取
 */
@Component
@Log4j2
public class CrawlerDocPageProcessor extends AbstractCrawlerPageProcessor {

    @Autowired
    private CrawlerConfigProperty crawlerConfigProperty;

    @Autowired
    private CrawlerHelper crawlerHelper;

    /**
     * 处理页面数据
     *
     * @param page
     */
    @Override
    public void handelPage(Page page) {
        long currentTime = System.currentTimeMillis();
        String handelType = crawlerHelper.getHandelType(page.getRequest());
        log.info("开始解析目标页数据，url:{},handelType:{}", page.getUrl(), handelType);
        //获取抓取规则列表
        List<ParseRule> targetParseRuleList = crawlerConfigProperty.getTargetParseRuleList();
        //抽取有效的数据
        targetParseRuleList = ParseRuleUtils.parseHtmlByRuleList(page.getHtml(), targetParseRuleList);
        if (null != targetParseRuleList && !targetParseRuleList.isEmpty()) {
            for (ParseRule parseRule : targetParseRuleList) {
                //将数据添加进page里面，交给后续的pipline 处理
                log.info("添加数据字段到field，url:{}，handelType:{},field:{}", page.getUrl(), handelType, parseRule.getField());
                page.putField(parseRule.getField(), parseRule.getMergeContent());
            }
        }

        log.info("解析目标页数据完成，url:{},handelType:{},耗时：{}", page.getUrl(), handelType, System.currentTimeMillis() - currentTime);
    }

    /**
     * 需要处理的爬取类型
     * 所以的爬取类型都处理
     *
     * @param handelType
     * @return
     */
    @Override
    public boolean isNeedHandelType(String handelType) {
        return true;
    }

    /**
     * 需要处理的文档类型
     * 只处理目标类型
     *
     * @param documentType
     * @return
     */
    @Override
    public boolean isNeedDocumentType(String documentType) {
        return CrawlerEnum.DocumentType.PAGE.name().equals(documentType);
    }


    @Override
    public int getPriority() {
        return 120;
    }
}

