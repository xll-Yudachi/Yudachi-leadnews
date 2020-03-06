package com.yudachi.behavior.service.impl;

import com.yudachi.behavior.service.AppUnLikesBehaviorService;
import com.yudachi.model.behavior.dtos.UnLikesBehaviorDto;
import com.yudachi.model.behavior.pojos.ApBehaviorEntry;
import com.yudachi.model.behavior.pojos.ApUnlikesBehavior;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.common.enums.AppHttpCodeEnum;
import com.yudachi.model.mappers.app.ApBehaviorEntryMapper;
import com.yudachi.model.mappers.app.ApUnlikesBehaviorMapper;
import com.yudachi.model.user.pojos.ApUser;
import com.yudachi.utils.threadlocal.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@SuppressWarnings("all")
public class AppUnLikesBehaviorServiceImpl implements AppUnLikesBehaviorService {

    @Autowired
    private ApUnlikesBehaviorMapper apUnLikesBehaviorMapper;
    @Autowired
    private ApBehaviorEntryMapper apBehaviorEntryMapper;

    @Override
    public ResponseResult saveUnLikesBehavior(UnLikesBehaviorDto dto) {
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
        ApUnlikesBehavior apUnlikesBehavior = new ApUnlikesBehavior();
        apUnlikesBehavior.setEntryId(apBehaviorEntry.getId());
        apUnlikesBehavior.setCreatedTime(new Date());
        apUnlikesBehavior.setArticleId(dto.getArticleId());
        apUnlikesBehavior.setType(dto.getType());
        return ResponseResult.okResult(apUnLikesBehaviorMapper.insert(apUnlikesBehavior));
    }
}
