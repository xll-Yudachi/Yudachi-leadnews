package com.yudachi.crawler;

import com.yudachi.crawler.service.AdLabelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = CrawlerApplication.class)
@RunWith(SpringRunner.class)
@SuppressWarnings("all")
public class AdLabelServiceTest {

    @Autowired
    private AdLabelService labelService;

    @Test
    public void testGetLabelIds(){

        String labelIds = labelService.getLabelIds("java,web,yudachi");
        System.out.println(labelIds);
    }

    @Test
    public void testGetAdChannelByLabelIds(){
        Integer adChannelByLabelIds = labelService.getAdChannelByLabelIds("1,2");
        System.out.println(adChannelByLabelIds);
    }
}