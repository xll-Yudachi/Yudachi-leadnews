package com.yudachi.admin.apis;

import com.yudachi.model.admin.pojos.AdUser;
import com.yudachi.model.common.dtos.ResponseResult;

public interface LoginControllerApi {
    public ResponseResult login(AdUser user);
}
