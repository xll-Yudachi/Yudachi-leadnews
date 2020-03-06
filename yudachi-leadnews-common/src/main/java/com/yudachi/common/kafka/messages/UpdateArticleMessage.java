package com.yudachi.common.kafka.messages;

import com.yudachi.common.kafka.KafkaMessage;
import com.yudachi.model.mess.app.UpdateArticle;

public class UpdateArticleMessage extends KafkaMessage<UpdateArticle> {

    public UpdateArticleMessage(){}

    public UpdateArticleMessage(UpdateArticle data) {
        super(data);
    }
    @Override
    public String getType() {
        return "update-article";
    }
}