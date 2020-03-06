package com.yudachi.article.job;

import com.yudachi.article.service.AppHotArticleService;
import com.yudachi.common.quartz.AbstractJob;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class AppHotArticleQuartz extends AbstractJob {
    @Override
    public String[] triggerCron() {
        return new String[]{"0 0/2 * * * ?"};
    }

    @Autowired
    private AppHotArticleService appHotArticleService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        long currentTimeMillis = System.currentTimeMillis();
        log.info("开始计算热文章数据");
        appHotArticleService.computeHotArticle();
        log.info("计算热文章数据完成，耗时:{}",System.currentTimeMillis()-currentTimeMillis);
    }

    @Override
    public String descTrigger() {
        return "每天00:05分执行一次";
    }
}