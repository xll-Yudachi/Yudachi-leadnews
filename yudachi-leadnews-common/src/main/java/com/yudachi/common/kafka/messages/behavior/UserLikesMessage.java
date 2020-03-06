package com.yudachi.common.kafka.messages.behavior;


import com.yudachi.common.kafka.KafkaMessage;
import com.yudachi.model.behavior.pojos.ApLikesBehavior;

public class UserLikesMessage extends KafkaMessage<ApLikesBehavior> {

    public UserLikesMessage(){}

    public UserLikesMessage(ApLikesBehavior data){
        super(data);
    }

    @Override
    public String getType() {
        return "user-likes";
    }
}
