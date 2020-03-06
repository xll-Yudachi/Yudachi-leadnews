package com.yudachi.migration.quartz;

import com.yudachi.common.quartz.AbstractJob;
import com.yudachi.migration.service.ApHotArticleService;
import com.yudachi.model.article.pojos.ApHotArticles;
import lombok.extern.log4j.Log4j2;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定期删除过期的数据
 */
@Component
@Log4j2
public class MigrationDeleteHotDataQuartz extends AbstractJob {

    @Autowired
    private ApHotArticleService apHotArticleService;


    @Override
    public String[] triggerCron() {
        /**
         * 2019/8/9 22:30:00
         * 2019/8/10 22:30:00
         * 2019/8/11 22:30:00
         * 2019/8/12 22:30:00
         * 2019/8/13 22:30:00
         */
        return new String[]{"0 30 22 * * ?"};
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        long cutrrentTime = System.currentTimeMillis();
        log.info("开始删除数据库过期数据");
        deleteExpireHotData();
        log.info("删除数据库过期数据结束，耗时:{}", System.currentTimeMillis() - cutrrentTime);
    }

    /**
     * 删除过期的热数据
     */
    public void deleteExpireHotData() {
        List<ApHotArticles> apHotArticlesList = apHotArticleService.selectExpireMonth();
        if (null != apHotArticlesList && !apHotArticlesList.isEmpty()) {
            for (ApHotArticles apHotArticle : apHotArticlesList) {
                apHotArticleService.deleteHotData(apHotArticle);
            }
        }
    }

}