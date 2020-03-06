package com.yudachi.user.service.impl;

import com.yudachi.common.zookeeper.Sequences;
import com.yudachi.model.behavior.dtos.FollowBehaviorDto;
import com.yudachi.model.behavior.pojos.ApBehaviorEntry;
import com.yudachi.model.behavior.pojos.ApFollowBehavior;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.common.enums.AppHttpCodeEnum;
import com.yudachi.model.mappers.app.ApBehaviorEntryMapper;
import com.yudachi.model.mappers.app.ApFollowBehaviorMapper;
import com.yudachi.user.service.AppFollowBehaviorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@SuppressWarnings("all")
public class AppFollowBehaviorServiceImpl implements AppFollowBehaviorService {

    private static Logger logger = LoggerFactory.getLogger(AppFollowBehaviorServiceImpl.class);

    @Autowired
    private ApFollowBehaviorMapper apFollowBehaviorMapper;
    @Autowired
    private ApBehaviorEntryMapper apBehaviorEntryMapper;
    @Autowired
    private Sequences sequences;

    @Override
    // 开启异步 存储关注行为是一个可选需求，因此采用异步存储的方式，提升关注接口的性能，并注意@Async的方法和调用方法不能存放同一个类中。当前行为的出错 不影响其他功能的实现
    @Async
    public ResponseResult saveFollowBehavior(Long userId, FollowBehaviorDto dto) {
        if (userId == null && dto.getEquipmentId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        ApBehaviorEntry apBehaviorEntry = apBehaviorEntryMapper.selectByUserIdOrEquipment(userId == null ? null : userId.intValue(), dto.getEquipmentId());
        if (apBehaviorEntry == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApFollowBehavior apFollowBehavior = new ApFollowBehavior();
        apFollowBehavior.setEntryId(apBehaviorEntry.getId());
        apFollowBehavior.setCreatedTime(new Date());
        apFollowBehavior.setArticleId(dto.getArticleId());
        apFollowBehavior.setFollowId(dto.getFollowId());
        return ResponseResult.okResult(apFollowBehaviorMapper.insert(apFollowBehavior));


    }
}
