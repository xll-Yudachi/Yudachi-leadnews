package com.yudachi.common.kafka.messages.app;


import com.yudachi.common.kafka.KafkaMessage;
import com.yudachi.model.mess.app.ArticleVisitStreamDto;

public class ArticleVisitStreamMessage extends KafkaMessage<ArticleVisitStreamDto> {

    public ArticleVisitStreamMessage(){}

    public ArticleVisitStreamMessage(ArticleVisitStreamDto data){
        super(data);
    }

    @Override
    public String getType() {
        return "article-visit-stream";
    }
}
