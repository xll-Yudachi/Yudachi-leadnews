package com.yudachi.media.kafka;

import com.yudachi.common.kafka.KafkaSender;
import com.yudachi.common.kafka.messages.SubmitArticleAuthMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("all")
public class AdminMessageSender {

    @Autowired
    KafkaSender kafkaSender;

    /**
     * 只发送行为消息
     * @param message
     */
    @Async
    public void sendMessage(SubmitArticleAuthMessage message){
        kafkaSender.sendSubmitArticleAuthMessage(message);
    }


}
