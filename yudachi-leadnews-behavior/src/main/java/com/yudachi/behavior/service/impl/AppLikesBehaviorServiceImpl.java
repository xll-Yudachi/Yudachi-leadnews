package com.yudachi.behavior.service.impl;

import com.yudachi.behavior.kafka.BehaviorMessageSender;
import com.yudachi.behavior.service.AppLikesBehaviorService;
import com.yudachi.common.kafka.messages.behavior.UserLikesMessage;
import com.yudachi.common.zookeeper.Sequences;
import com.yudachi.model.behavior.dtos.LikesBehaviorDto;
import com.yudachi.model.behavior.pojos.ApBehaviorEntry;
import com.yudachi.model.behavior.pojos.ApLikesBehavior;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.common.enums.AppHttpCodeEnum;
import com.yudachi.model.mappers.app.ApBehaviorEntryMapper;
import com.yudachi.model.mappers.app.ApLikesBehaviorMapper;
import com.yudachi.model.user.pojos.ApUser;
import com.yudachi.utils.common.BurstUtils;
import com.yudachi.utils.threadlocal.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@SuppressWarnings("all")
public class AppLikesBehaviorServiceImpl implements AppLikesBehaviorService {

    @Autowired
    private ApLikesBehaviorMapper apLikesBehaviorMapper;
    @Autowired
    private ApBehaviorEntryMapper apBehaviorEntryMapper;
    @Autowired
    private Sequences sequences;
    @Autowired
    private BehaviorMessageSender behaviorMessageSender;

    @Override
    public ResponseResult saveLikesBehavior(LikesBehaviorDto dto) {
        ApUser user = AppThreadLocalUtils.getUser();
        // 用户和设备不能同时为空
        if (user == null && dto.getEquipmentId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        Long userId = null;
        if (user != null) {
            userId = user.getId();
        }
        ApBehaviorEntry apBehaviorEntry = apBehaviorEntryMapper.selectByUserIdOrEquipment(userId == null ? null : userId.intValue(), dto.getEquipmentId());
        if (apBehaviorEntry == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApLikesBehavior apLikesBehavior = new ApLikesBehavior();
        apLikesBehavior.setId(sequences.sequenceApLikes());
        apLikesBehavior.setBehaviorEntryId(apBehaviorEntry.getId());
        apLikesBehavior.setCreatedTime(new Date());
        apLikesBehavior.setEntryId(dto.getEntryId());
        apLikesBehavior.setType(dto.getType());
        apLikesBehavior.setOperation(dto.getOperation());
        apLikesBehavior.setBurst(BurstUtils.encrypt(apLikesBehavior.getId(),apLikesBehavior.getBehaviorEntryId()));

        int insert = apLikesBehaviorMapper.insert(apLikesBehavior);
        if(insert==1){
            if(apLikesBehavior.getOperation()==ApLikesBehavior.Operation.LIKE.getCode()){
                behaviorMessageSender.sendMessagePlus(new UserLikesMessage(apLikesBehavior),userId,true);
            }else if(apLikesBehavior.getOperation()==ApLikesBehavior.Operation.CANCEL.getCode()){
                behaviorMessageSender.sendMessageReduce(new UserLikesMessage(apLikesBehavior),userId,true);
            }
        }
        return ResponseResult.okResult(insert);
    }

}
