package com.yudachi.user.service;

import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.user.dtos.UserRelationDto;

public interface AppUserRelationService {
    public ResponseResult follow(UserRelationDto dto);
}
