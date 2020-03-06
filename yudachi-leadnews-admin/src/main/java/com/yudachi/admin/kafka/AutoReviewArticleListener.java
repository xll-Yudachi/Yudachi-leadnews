package com.yudachi.admin.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudachi.admin.service.ReviewCrawlerArticleService;
import com.yudachi.admin.service.ReviewMediaArticleService;
import com.yudachi.common.kafka.KafkaListener;
import com.yudachi.common.kafka.KafkaTopicConfig;
import com.yudachi.common.kafka.messages.SubmitArticleAuthMessage;
import com.yudachi.model.mess.admin.SubmitArticleAuto;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
public class AutoReviewArticleListener implements KafkaListener {

    @Autowired
    private KafkaTopicConfig kafkaTopicConfig;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private ReviewMediaArticleService reviewMediaArticleService;
    @Autowired
    private ReviewCrawlerArticleService reviewCrawlerArticleService;

    @Override
    public String topic() {
        return kafkaTopicConfig.getSubmitArticleAuth();
    }

    @Override
    public void onMessage(ConsumerRecord consumerRecord, Consumer consumer) {
        String value = (String) consumerRecord.value();
        log.info("接收到的消息为：{}" + value);
        try {
            SubmitArticleAuthMessage message = mapper.readValue(value, SubmitArticleAuthMessage.class);
            if (message != null) {
                SubmitArticleAuto.ArticleType type = message.getData().getType();
                if (type == SubmitArticleAuto.ArticleType.WEMEDIA) {
                    Integer articleId = message.getData().getArticleId();
                    if (articleId != null) {
                        //审核文章信息
                        reviewMediaArticleService.autoReviewArticleByMedia(articleId);
                    }
                }else if (type == SubmitArticleAuto.ArticleType.CRAWLER){
                    Integer articleId = message.getData().getArticleId();
                    if (articleId != null){
                        // 审核爬虫文章信息
                        try {
                            reviewCrawlerArticleService.autoReviewArticleByCrawler(articleId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("处理自动审核文章错误:[{}],{}", value, e);
            throw new RuntimeException("WS消息处理错误", e);
        }
    }

    @Override
    public void onMessage(Object o) {

    }
}
