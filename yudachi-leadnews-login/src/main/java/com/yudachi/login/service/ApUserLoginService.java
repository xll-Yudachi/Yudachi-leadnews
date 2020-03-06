package com.yudachi.login.service;

import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.user.pojos.ApUser;

public interface ApUserLoginService {
    /**
     * 用户登录验证
     * @param user
     * @return
     */
    ResponseResult loginAuth(ApUser user);

    /**
     * 用户登录验证V2
     * @param user
     * @return
     */
    ResponseResult loginAuthV2(ApUser user);
}
