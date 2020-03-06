package com.yudachi.media.service;

import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.media.pojos.WmUser;

public interface UserLoginService {
    ResponseResult login(WmUser user);
}
