package com.yudachi.article.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudachi.article.service.AppArticleService;
import com.yudachi.common.kafka.KafkaListener;
import com.yudachi.common.kafka.KafkaTopicConfig;
import com.yudachi.common.kafka.messages.app.ArticleVisitStreamMessage;
import com.yudachi.model.mess.app.ArticleVisitStreamDto;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 增量文章状态处理
 */
@Component
@Log4j2
public class ArticleIncrHandleListener implements KafkaListener<String,String> {

    @Autowired
    private KafkaTopicConfig kafkaTopicConfig;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private AppArticleService appArticleService;

    @Override
    public String topic() {
        return kafkaTopicConfig.getArticleIncrHandle();
    }

    @Override
    public void onMessage(ConsumerRecord<String, String> data, Consumer<?, ?> consumer) {
        log.info("receive Article Incr Handle message:{}",data);
        String value = (String)data.value();
        try {
            ArticleVisitStreamMessage message = mapper.readValue(value, ArticleVisitStreamMessage.class);
            ArticleVisitStreamDto dto = message.getData();
            appArticleService.updateArticleView(dto);
        }catch (Exception e){
            log.error("kafka send message[class:{}] to Article Incr Handle failed:{}","ArticleIncrHandle.class",e);
        }
    }
}
