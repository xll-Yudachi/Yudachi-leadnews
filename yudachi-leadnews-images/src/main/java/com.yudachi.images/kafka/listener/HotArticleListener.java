package com.yudachi.images.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudachi.common.kafka.KafkaListener;
import com.yudachi.common.kafka.KafkaTopicConfig;
import com.yudachi.common.kafka.messages.app.ApHotArticleMessage;
import com.yudachi.images.service.HotArticleImageService;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class HotArticleListener implements KafkaListener<String,String> {

    @Autowired
    private KafkaTopicConfig kafkaTopicConfig;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private HotArticleImageService hotArticleImageService;

    @Override
    public String topic() {
        return kafkaTopicConfig.getHotArticle();
    }

    @Override
    public void onMessage(ConsumerRecord<String, String> data, Consumer<?, ?> consumer) {
        log.info("receive hot article message:{}",data);
        String value = (String)data.value();
        try {
            ApHotArticleMessage message = mapper.readValue(value, ApHotArticleMessage.class);
            hotArticleImageService.handleHotImage(message);
        }catch (Exception e){
            log.error("kafka send message[class:{}] to handleHotImage failed:{}","ApHotArticleMessage.class",e);
        }
    }
}
