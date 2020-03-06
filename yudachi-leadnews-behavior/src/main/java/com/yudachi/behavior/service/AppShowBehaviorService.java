package com.yudachi.behavior.service;

import com.yudachi.model.behavior.dtos.ShowBehaviorDto;
import com.yudachi.model.common.dtos.ResponseResult;

public interface AppShowBehaviorService {
    /**
     * 存储行为数据
     * @param dto
     * @return
     */
    ResponseResult saveShowBehavior(ShowBehaviorDto dto);
}
