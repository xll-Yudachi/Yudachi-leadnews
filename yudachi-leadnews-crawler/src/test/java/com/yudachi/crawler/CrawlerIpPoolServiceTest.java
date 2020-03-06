package com.yudachi.crawler;

import com.yudachi.crawler.service.CrawlerIpPoolService;
import com.yudachi.model.crawler.pojos.ClIpPool;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CrawlerIpPoolServiceTest {

    @Autowired
    private CrawlerIpPoolService crawlerIpPoolService;

    @Test
    public void testSaveCrawlerIpPool(){
        ClIpPool clIpPool = new ClIpPool();
        clIpPool.setIp("2222.3333.444.5555");
        clIpPool.setPort(1111);
        clIpPool.setEnable(true);
        clIpPool.setCreatedTime(new Date());
        crawlerIpPoolService.saveCrawlerIpPool(clIpPool);
    }


    @Test
    public void testCheckExist(){
        boolean b = crawlerIpPoolService.checkExist("2222.3333.444.5555", 1111);
        System.out.println(b);
    }
}