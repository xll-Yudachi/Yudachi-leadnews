package com.yudachi.media.service.impl;

import com.google.common.collect.Maps;
import com.yudachi.media.service.UserLoginService;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.common.enums.AppHttpCodeEnum;
import com.yudachi.model.mappers.wemedia.WmUserMapper;
import com.yudachi.model.media.pojos.WmUser;
import com.yudachi.utils.jwt.AppJwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@SuppressWarnings("all")
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    private WmUserMapper wmUserMapper;

    @Override
    public ResponseResult login(WmUser user) {
        if (StringUtils.isEmpty(user.getName()) && StringUtils.isEmpty(user.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE, "用户名和密码不能为空");
        }
        WmUser dbUser = wmUserMapper.selectByName(user.getName());
        if (dbUser != null) {
            if (user.getPassword().equals(dbUser.getPassword())) {
                Map<String, Object> map = Maps.newHashMap();
                dbUser.setPassword("");
                dbUser.setSalt("");
                map.put("token", AppJwtUtil.getToken(dbUser));
                map.put("user", dbUser);
                return ResponseResult.okResult(map);
            } else {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
        } else {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户不存在");
        }
    }
}
