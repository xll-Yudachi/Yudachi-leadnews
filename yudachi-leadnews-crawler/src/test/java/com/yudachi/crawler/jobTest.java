package com.yudachi.crawler;

import com.yudachi.crawler.service.AdLabelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest()
@RunWith(SpringRunner.class)
@SuppressWarnings("all")
public class jobTest {

    @Autowired
    private AdLabelService labelService;

    @Test
    public void test(){
        while (true){

        }
    }

}