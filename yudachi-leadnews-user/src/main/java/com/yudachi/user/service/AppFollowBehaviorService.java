package com.yudachi.user.service;

import com.yudachi.model.behavior.dtos.FollowBehaviorDto;
import com.yudachi.model.common.dtos.ResponseResult;

public interface AppFollowBehaviorService {

    /**
     * @Params [dto]
     * @Return com.yudachi.model.common.dtos.ResponseResult
     * @Description 存储关注数据
     **/
    public ResponseResult saveFollowBehavior(Long userid, FollowBehaviorDto dto);
}
