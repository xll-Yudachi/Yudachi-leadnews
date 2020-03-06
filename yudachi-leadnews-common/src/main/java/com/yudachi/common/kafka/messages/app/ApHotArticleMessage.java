package com.yudachi.common.kafka.messages.app;

import com.yudachi.common.kafka.KafkaMessage;
import com.yudachi.model.article.pojos.ApHotArticles;

public class ApHotArticleMessage extends KafkaMessage<ApHotArticles> {

    public ApHotArticleMessage() {
    }

    public ApHotArticleMessage(ApHotArticles data) {
        super(data);
    }

    @Override
    public String getType() {
        return "hot-article";
    }
}
