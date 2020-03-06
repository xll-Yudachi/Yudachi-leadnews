package com.yudachi.migration.quartz;

import com.yudachi.common.quartz.AbstractJob;
import com.yudachi.migration.service.ArticleQuantityService;
import lombok.extern.log4j.Log4j2;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
@Log4j2
/**
 * 全量数据从mysql 同步到HBase
 */
public class MigrationDbToHBaseQuartz extends AbstractJob {

    @Autowired
    private ArticleQuantityService articleQuantityService;


    @Override
    public String[] triggerCron() {
        /**
         * 2019/8/9 10:15:00
         * 2019/8/9 10:20:00
         * 2019/8/9 10:25:00
         * 2019/8/9 10:30:00
         * 2019/8/9 10:35:00
         */
        return new String[]{"0 0/5 * * * ?"};
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("开始进行数据库到HBASE同步任务");
        articleQuantityService.dbToHbase();
        log.info("数据库到HBASE同步任务完成");
    }

}