package com.yudachi.admin.kafka;

import com.yudachi.common.kafka.KafkaListener;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

@Component
public class Listener implements KafkaListener {
    @Override
    public String topic() {
        return "topic.test";
    }

    @Override
    public void onMessage(ConsumerRecord consumerRecord, Consumer consumer) {
        System.err.println(1);
        System.err.println("接收到的消息=====" + consumerRecord);
    }

    @Override
    public void onMessage(Object o) {

    }
}
