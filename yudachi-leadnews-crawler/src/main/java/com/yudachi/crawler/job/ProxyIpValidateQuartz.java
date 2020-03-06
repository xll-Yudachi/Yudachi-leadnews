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
public class ProxyIpValidateQuartz extends AbstractJob {

    @Override
    public String[] triggerCron() {
        return new String[]{"0 0/30 * * * ?"};
    }

    @Autowired
    private ProxyIpManager proxyIpManager;

    @Autowired
    private CrawlerProxyProvider crawlerProxyProvider;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        long currentTimeMillis = System.currentTimeMillis();
        log.info("开始检验代理ip");
        proxyIpManager.validateProxyIp();
        crawlerProxyProvider.updateProxy();
        log.info("检验代理ip完成，耗时:{}",System.currentTimeMillis()-currentTimeMillis);
    }
}