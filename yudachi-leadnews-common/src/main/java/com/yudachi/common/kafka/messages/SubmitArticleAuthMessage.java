package com.yudachi.common.kafka.messages;

import com.yudachi.common.kafka.KafkaMessage;
import com.yudachi.model.mess.admin.SubmitArticleAuto;

public class SubmitArticleAuthMessage extends KafkaMessage<SubmitArticleAuto> {

    public SubmitArticleAuthMessage() {
    }

    public SubmitArticleAuthMessage(SubmitArticleAuto data) {
        super(data);
    }

    @Override
    protected String getType() {
        return "submit-article-auth";
    }
}
