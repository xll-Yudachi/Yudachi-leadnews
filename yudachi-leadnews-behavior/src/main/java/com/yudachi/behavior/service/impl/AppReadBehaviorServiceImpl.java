package com.yudachi.behavior.service.impl;

import com.yudachi.behavior.service.AppReadBehaviorService;
import com.yudachi.common.zookeeper.Sequences;
import com.yudachi.model.behavior.dtos.ReadBehaviorDto;
import com.yudachi.model.behavior.pojos.ApBehaviorEntry;
import com.yudachi.model.behavior.pojos.ApReadBehavior;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.common.enums.AppHttpCodeEnum;
import com.yudachi.model.mappers.app.ApBehaviorEntryMapper;
import com.yudachi.model.mappers.app.ApReadBehaviorMapper;
import com.yudachi.model.user.pojos.ApUser;
import com.yudachi.utils.common.BurstUtils;
import com.yudachi.utils.threadlocal.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author Yudachi
 * @Date 2020/2/15 23:39
 * @Version 1.0
 * @Description 阅读行为可能发生多次，第一次是新增数据，第二次是更新阅读时间，最大阅读比例，阅读次数等信息。
 **/
@Service
@SuppressWarnings("all")
public class AppReadBehaviorServiceImpl implements AppReadBehaviorService {
    @Autowired
    private ApReadBehaviorMapper apReadBehaviorMapper;
    @Autowired
    private ApBehaviorEntryMapper apBehaviorEntryMapper;
    @Autowired
    private Sequences sequences;

    @Override
    public ResponseResult saveReadBehavior(ReadBehaviorDto dto){

        ApUser user = AppThreadLocalUtils.getUser();
        // 用户和设备不能同时为空
        if(user==null&& dto.getEquipmentId()==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        Long userId = null;
        if(user!=null){
            userId = user.getId();
        }
        ApBehaviorEntry apBehaviorEntry = apBehaviorEntryMapper.selectByUserIdOrEquipment(userId == null ? null : userId.intValue(), dto.getEquipmentId());
        if(apBehaviorEntry==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApReadBehavior apReadBehavior = apReadBehaviorMapper.selectByEntryId(BurstUtils.groudOne(apBehaviorEntry.getId()),apBehaviorEntry.getId(),dto.getArticleId());
        boolean isInsert = false;
        if(apReadBehavior==null){
            apReadBehavior = new ApReadBehavior();
            apReadBehavior.setId(sequences.sequenceApReadBehavior());
            isInsert = true;
        }
        apReadBehavior.setEntryId(apBehaviorEntry.getId());
        apReadBehavior.setCount(dto.getCount());
        apReadBehavior.setPercentage(dto.getPercentage());
        apReadBehavior.setArticleId(dto.getArticleId());
        apReadBehavior.setLoadDuration(dto.getLoadDuration());
        apReadBehavior.setReadDuration(dto.getReadDuration());
        apReadBehavior.setCreatedTime(new Date());
        apReadBehavior.setUpdatedTime(new Date());
        apReadBehavior.setBurst(BurstUtils.encrypt(apReadBehavior.getId(),apReadBehavior.getEntryId()));
        // 插入
        if(isInsert){
            return ResponseResult.okResult(apReadBehaviorMapper.insert(apReadBehavior));
        }else {
            // 更新
            return ResponseResult.okResult(apReadBehaviorMapper.update(apReadBehavior));
        }
    }
}
