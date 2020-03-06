package com.yudachi.crawler.job;

import com.yudachi.common.quartz.AbstractJob;
import com.yudachi.crawler.manager.ProxyIpManager;
import com.yudachi.model.crawler.core.proxy.CrawlerProxyProvider;
import lombok.extern.log4j.Log4j2;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 代理Ip定时管理类
 */
@Component
@DisallowConcurrentExecution
@Log4j2
/**
 * 代理IP 更新定时任务
 */
public class ProxyIpUpdateQuartz extends AbstractJob {

    @Autowired
    private ProxyIpManager proxyIpManager;

    @Autowired
    private CrawlerProxyProvider crawlerProxyProvider;


    @Override
    public String[] triggerCron() {
        /**
         * 2019/8/9 10:30:00
         * 2019/8/9 11:00:00
         * 2019/8/9 11:30:00
         * 2019/8/9 12:00:00
         * 2019/8/9 12:30:00
         */
        return new String[]{"0 0/30 * * * ?"};
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        long cutrrentTime = System.currentTimeMillis();
        log.info("开始更新代理IP");
        proxyIpManager.updateProxyIp();
        crawlerProxyProvider.updateProxy();
        log.info("更新代理IP完成，耗时：{}",System.currentTimeMillis()-cutrrentTime);
    }
}