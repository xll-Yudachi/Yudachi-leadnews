package com.yudachi.media.apis;

import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.media.pojos.WmUser;

public interface LoginControllerApi {
    // 自媒体用户登录
    public ResponseResult login(WmUser user);


}
