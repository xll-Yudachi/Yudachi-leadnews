package com.yudachi.common.kafka.messages.behavior;


import com.yudachi.common.kafka.KafkaMessage;
import com.yudachi.model.behavior.pojos.ApReadBehavior;

public class UserReadMessage extends KafkaMessage<ApReadBehavior> {

    public UserReadMessage(){}

    public UserReadMessage(ApReadBehavior data){
        super(data);
    }

    @Override
    public String getType() {
        return "user-read";
    }
}
