package com.yudachi.admin.service.impl;

import com.yudachi.admin.service.UserLoginService;
import com.yudachi.model.admin.pojos.AdUser;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.common.enums.AppHttpCodeEnum;
import com.yudachi.model.mappers.admin.AdUserMapper;
import com.yudachi.utils.jwt.AppJwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@SuppressWarnings("all")
public class UserLoginServiceImpl implements UserLoginService {
    @Autowired
    private AdUserMapper adUserMapper;

    @Override
    public ResponseResult login(AdUser user) {
        if (StringUtils.isEmpty(user.getName()) && StringUtils.isEmpty(user.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE, "用户名和密码不能为空");
        }

        AdUser adUser = adUserMapper.selectByName(user.getName());
        if (adUser == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户不存在");
        } else {
            if (user.getPassword().equalsIgnoreCase(adUser.getPassword())) {
                Map<String, Object> map = new HashMap<>();
                adUser.setPassword("");
                adUser.setSalt("");
                map.put("token", AppJwtUtil.getToken(adUser));
                map.put("user", adUser);
                return ResponseResult.okResult(map);
            } else {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
        }
    }
}
