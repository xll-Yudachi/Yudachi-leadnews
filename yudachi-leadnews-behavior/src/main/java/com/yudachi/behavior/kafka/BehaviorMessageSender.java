package com.yudachi.behavior.kafka;

import com.yudachi.common.kafka.KafkaMessage;
import com.yudachi.common.kafka.KafkaSender;
import com.yudachi.common.kafka.messages.UpdateArticleMessage;
import com.yudachi.common.kafka.messages.behavior.UserLikesMessage;
import com.yudachi.common.kafka.messages.behavior.UserReadMessage;
import com.yudachi.model.behavior.pojos.ApLikesBehavior;
import com.yudachi.model.mess.app.UpdateArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class BehaviorMessageSender {

    @Autowired
    private KafkaSender kafkaSender;

    /**
     * 发送+1的消息
     * @param message
     * @param isSendToArticle
     */
    @Async
    public void sendMessagePlus(KafkaMessage message, Long apUserId, boolean isSendToArticle){
        if(isSendToArticle){
            UpdateArticleMessage temp = parseMessage(message,apUserId,1);
            if(temp!=null)
                kafkaSender.sendArticleUpdateBus(temp);
        }
    }

    /**
     * 发送-1的消息
     * @param message
     * @param isSendToArticle
     */
    @Async
    public void sendMessageReduce(KafkaMessage message,Long apUserId,boolean isSendToArticle){
        if(isSendToArticle){
            UpdateArticleMessage temp = parseMessage(message,apUserId,-1);
            if(temp!=null)
                kafkaSender.sendArticleUpdateBus(temp);
        }
    }

    /**
     * 转换行为消息为修改位置的消息
     * @param message
     * @param step
     * @return
     */
    private UpdateArticleMessage parseMessage(KafkaMessage message, Long apUserId, int step) {
        UpdateArticle updateArticle = new UpdateArticle();
        if(apUserId!=null){
            updateArticle.setApUserId(apUserId.intValue());
        }
        if(message instanceof UserLikesMessage){
            UserLikesMessage likesMessage = (UserLikesMessage) message;
            //只处理文章数据的点赞
            if(likesMessage.getData().getType() == ApLikesBehavior.Type.ARTICLE.getCode()){
                updateArticle.setType(UpdateArticle.UpdateArticleType.LIKES);
                updateArticle.setAdd(step);
                updateArticle.setArticleId(likesMessage.getData().getEntryId());
                updateArticle.setBehaviorId(likesMessage.getData().getBehaviorEntryId());
            }

        }else if(message instanceof UserReadMessage){
            UserReadMessage userReadMessage = (UserReadMessage) message;
            updateArticle.setType(UpdateArticle.UpdateArticleType.VIEWS);
            updateArticle.setAdd(step);
            updateArticle.setArticleId(userReadMessage.getData().getArticleId());
            updateArticle.setBehaviorId(userReadMessage.getData().getEntryId());
        }

        if(updateArticle.getArticleId()!=null){
            return new UpdateArticleMessage(updateArticle);
        }
        return null;

    }


}