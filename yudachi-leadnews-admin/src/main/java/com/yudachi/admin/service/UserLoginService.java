package com.yudachi.admin.service;

import com.yudachi.model.admin.pojos.AdUser;
import com.yudachi.model.common.dtos.ResponseResult;

public interface UserLoginService {
    ResponseResult login(AdUser user);
}
