package com.yudachi.crawler.job;

import com.yudachi.common.quartz.AbstractJob;
import com.yudachi.crawler.manager.ProcessingFlowManager;
import lombok.extern.log4j.Log4j2;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
@Log4j2
public class CrawlerForwardQuartz extends AbstractJob {

    @Autowired
    private ProcessingFlowManager processingFlowManager;

    @Override
    public String[] triggerCron() {
        return new String[]{"0/5 * * * * ?"};
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        long cutrrentTime = System.currentTimeMillis();
        log.info("开始正向抓取");
        processingFlowManager.handle();
        log.info("正向抓取结束,耗时：", System.currentTimeMillis() - cutrrentTime);
    }
}
