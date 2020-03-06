package com.yudachi.behavior.service;

import com.yudachi.model.behavior.dtos.UnLikesBehaviorDto;
import com.yudachi.model.common.dtos.ResponseResult;

public interface AppUnLikesBehaviorService {
    /**
     * @Params [dto]
     * @Return com.yudachi.model.common.dtos.ResponseResult
     * @Description 存储不喜欢行为
     **/
    public ResponseResult saveUnLikesBehavior(UnLikesBehaviorDto dto);
}
