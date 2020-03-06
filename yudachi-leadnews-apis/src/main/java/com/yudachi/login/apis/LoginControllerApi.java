package com.yudachi.login.apis;

import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.user.pojos.ApUser;

public interface LoginControllerApi {
    public ResponseResult login(ApUser user);
}
