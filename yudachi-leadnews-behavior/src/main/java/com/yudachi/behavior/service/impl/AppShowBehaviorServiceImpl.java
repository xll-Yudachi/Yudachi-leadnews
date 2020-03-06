package com.yudachi.behavior.service.impl;

import com.yudachi.behavior.service.AppShowBehaviorService;
import com.yudachi.model.behavior.dtos.ShowBehaviorDto;
import com.yudachi.model.behavior.pojos.ApBehaviorEntry;
import com.yudachi.model.behavior.pojos.ApShowBehavior;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.common.enums.AppHttpCodeEnum;
import com.yudachi.model.mappers.app.ApBehaviorEntryMapper;
import com.yudachi.model.mappers.app.ApShowBehaviorMapper;
import com.yudachi.model.user.pojos.ApUser;
import com.yudachi.utils.threadlocal.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@SuppressWarnings("all")
public class AppShowBehaviorServiceImpl implements AppShowBehaviorService {

    @Autowired
    private ApShowBehaviorMapper apShowBehaviorMapper;
    @Autowired
    private ApBehaviorEntryMapper apBehaviorEntryMapper;


    @Override
    public ResponseResult saveShowBehavior(ShowBehaviorDto dto) {

        ApUser user = AppThreadLocalUtils.getUser();
        System.err.println(user);
        System.err.println(dto);
        // 用户和设备不能同时为空
        if (user == null && (dto.getArticleIds() == null)){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }

        Long userId = null;
        if (user != null){
            userId = user.getId();
        }
        ApBehaviorEntry apBehaviorEntry = apBehaviorEntryMapper.selectByUserIdOrEquipment(userId == null ? null : userId.intValue(), dto.getEquipmentId());

        // 行为实体找逻辑上这里是必定有值得，除非参数错误
        if(apBehaviorEntry==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 过滤新数据
        Integer[] temp = new Integer[dto.getArticleIds().size()];
        for (int i = 0; i < temp.length; i++) {
            temp[i]=dto.getArticleIds().get(i).getId();
        }
        List<ApShowBehavior> list = apShowBehaviorMapper.selectListByEntryIdAndArticleIds(apBehaviorEntry.getId(), temp);


        List<Integer> oriList = new ArrayList(Arrays.asList(temp));


        if(!list.isEmpty()){
            list.forEach(item->{
                oriList.remove(item.getArticleId());
            });
        }
        // 插入新数据
        if(!oriList.isEmpty()) {
            temp = new Integer[oriList.size()];
            oriList.toArray(temp);
            apShowBehaviorMapper.saveBehaviors(apBehaviorEntry.getId(), temp);
        }
        return ResponseResult.okResult(0);
    }
}
