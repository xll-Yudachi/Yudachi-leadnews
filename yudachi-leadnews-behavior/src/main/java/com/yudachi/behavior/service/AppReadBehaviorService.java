package com.yudachi.behavior.service;

import com.yudachi.model.behavior.dtos.ReadBehaviorDto;
import com.yudachi.model.common.dtos.ResponseResult;

public interface AppReadBehaviorService {
    /**
     * @Params [dto]
     * @Return com.yudachi.model.common.dtos.ResponseResult
     * @Description 存储阅读行为
     **/
    public ResponseResult saveReadBehavior(ReadBehaviorDto dto);
}
