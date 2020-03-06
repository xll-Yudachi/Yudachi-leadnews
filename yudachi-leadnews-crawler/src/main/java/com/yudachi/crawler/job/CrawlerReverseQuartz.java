package com.yudachi.crawler.job;

import com.yudachi.common.quartz.AbstractJob;
import com.yudachi.crawler.manager.ProcessingFlowManager;
import com.yudachi.crawler.service.CrawlerNewsAdditionalService;
import lombok.extern.log4j.Log4j2;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 逆向抓取
 */
@Component
@DisallowConcurrentExecution
@Log4j2
public class CrawlerReverseQuartz extends AbstractJob {

    @Autowired
    private ProcessingFlowManager processingFlowManager;
    @Autowired
    private CrawlerNewsAdditionalService crawlerNewsAdditionalService;

    @Override
    public String[] triggerCron() {
        /**
         * 2019/8/9 11:00:00
         * 2019/8/9 13:00:00
         * 2019/8/9 15:00:00
         * 2019/8/9 17:00:00
         * 2019/8/9 19:00:00
         */
        return new String[]{"0 0 0/1 * * ?"};
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        long cutrrentTime = System.currentTimeMillis();
        log.info("开始反向抓取");
        processingFlowManager.reverseHandel();
        log.info("反向抓取结束,耗时：", System.currentTimeMillis() - cutrrentTime);

    }
}