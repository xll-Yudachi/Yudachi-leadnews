package com.yudachi.crawler;

import com.yudachi.crawler.manager.ProcessingFlowManager;
import com.yudachi.crawler.process.entity.ProcessFlowData;
import com.yudachi.crawler.process.original.impl.CsdnOriginalDataProcess;
import com.yudachi.crawler.utils.SeleniumClient;
import com.yudachi.model.crawler.core.cookie.CrawlerHtml;
import com.yudachi.model.crawler.core.parse.ParseItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CsdnOriginalDataProcessTest {
    @Autowired
    private CsdnOriginalDataProcess csdnOriginalDataProcess;
    @Autowired
    private ProcessingFlowManager processingFlowManager;

    @Test
    public void test(){
        List<ParseItem> parseItems =
                csdnOriginalDataProcess.parseOriginalRequestData(new ProcessFlowData());
        System.out.println(parseItems);
    }

    @Autowired
    private SeleniumClient seleniumClient;

    @Test
    public void testSelenium(){
        CrawlerHtml crawlerHtml =  seleniumClient.getCrawlerHtml("http://www.baidu.com",null,null);
        System.out.println(crawlerHtml.getHtml());
    }

    @Test
    public void testCrawler(){
        processingFlowManager.handle();
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}