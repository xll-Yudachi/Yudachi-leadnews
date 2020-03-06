package com.yudachi.behavior.service;

import com.yudachi.model.behavior.dtos.LikesBehaviorDto;
import com.yudachi.model.common.dtos.ResponseResult;

public interface AppLikesBehaviorService {

    /**
     * @Author Yudachi
     * @Date 2020/2/15 23:21
     * @Version 1.0
     * @Description 记录点赞行为
     **/
    public ResponseResult saveLikesBehavior(LikesBehaviorDto dto);
}
