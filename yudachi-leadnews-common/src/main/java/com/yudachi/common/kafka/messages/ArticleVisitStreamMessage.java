package com.yudachi.common.kafka.messages;

import com.yudachi.common.kafka.KafkaMessage;
import com.yudachi.model.mess.app.ArticleVisitStreamDto;

public class ArticleVisitStreamMessage extends KafkaMessage<ArticleVisitStreamDto> {

    @Override
    public String getType() {
        return "article-visit-stream";
    }
}
