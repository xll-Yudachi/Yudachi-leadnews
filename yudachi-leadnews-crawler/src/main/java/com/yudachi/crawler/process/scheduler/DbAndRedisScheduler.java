package com.yudachi.crawler.process.scheduler;

import com.yudachi.crawler.helper.CrawlerHelper;
import com.yudachi.crawler.process.ProcessFlow;
import com.yudachi.crawler.process.entity.ProcessFlowData;
import com.yudachi.crawler.service.CrawlerNewsAdditionalService;
import com.yudachi.model.crawler.enums.CrawlerEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.RedisScheduler;

/**
 * URL防重复
 */
@Log4j2
public class DbAndRedisScheduler extends RedisScheduler implements ProcessFlow {

    @Autowired
    private CrawlerHelper crawlerHelper;

    @Autowired
    private CrawlerNewsAdditionalService crawlerNewsAdditionalService;

    public DbAndRedisScheduler(String host) {
        super(host);
    }

    public DbAndRedisScheduler(JedisPool pool) {
        super(pool);
    }

    /**
     * 是否重复
     * @param request request请求
     * @param task 任务
     * @return
     */
    @Override
    public boolean isDuplicate(Request request, Task task) {
        String handelType = crawlerHelper.getHandelType(request);
        boolean isExist = false;
        //正向统计才排重
        if (CrawlerEnum.HandelType.FORWARD.name().equals(handelType)) {
            log.info("URL排重开始，URL:{},documentType:{}", request.getUrl(), handelType);
            isExist = super.isDuplicate(request, task);
            if (!isExist) {
                isExist = crawlerNewsAdditionalService.isExistsUrl(request.getUrl());
            }
            log.info("URL排重结束，URL:{}，handelType:{},isExist：{}", request.getUrl(), handelType, isExist);
        } else {
            log.info("反向抓取，不进行URL排重");
        }
        return isExist;
    }


    @Override
    public void handle(ProcessFlowData processFlowData) {

    }

    @Override
    public CrawlerEnum.ComponentType getComponentType() {
        return CrawlerEnum.ComponentType.SCHEDULER;
    }

    @Override
    public int getPriority() {
        return 10000;
    }
}