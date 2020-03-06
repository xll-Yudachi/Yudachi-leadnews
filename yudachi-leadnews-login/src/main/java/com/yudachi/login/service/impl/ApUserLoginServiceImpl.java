package com.yudachi.login.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yudachi.common.kafka.KafkaSender;
import com.yudachi.login.service.ApUserLoginService;
import com.yudachi.login.service.ValidateService;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.common.enums.AppHttpCodeEnum;
import com.yudachi.model.mappers.app.ApUserMapper;
import com.yudachi.model.user.pojos.ApUser;
import com.yudachi.utils.jwt.AppJwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@SuppressWarnings("all")
public class ApUserLoginServiceImpl implements ApUserLoginService {

    @Autowired
    private ApUserMapper apUserMapper;

    @Override
    public ResponseResult loginAuth(ApUser user) {
        System.err.println(user);
        //验证参数
        if(StringUtils.isEmpty(user.getPhone()) || StringUtils.isEmpty(user.getPassword())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //查询用户
        ApUser dbUser = apUserMapper.selectByApPhone(user.getPhone());
        if(dbUser==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST);
        }
        //密码错误
        if(!user.getPassword().equals(dbUser.getPassword())){
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }
        dbUser.setPassword("");
        Map<String,Object> map = Maps.newHashMap();
        System.err.println(AppJwtUtil.getToken(dbUser));
        map.put("token", AppJwtUtil.getToken(dbUser));
        map.put("user",dbUser);
        return ResponseResult.okResult(map);

    }

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ValidateService validateService;
    private long USER_EXPIRE = 60 * 60 * 24l;
    @Autowired
    private KafkaSender kafkaSender;

    @Override
    public ResponseResult loginAuthV2(ApUser user) {
        //验证参数
        if(StringUtils.isEmpty(user.getPhone()) || StringUtils.isEmpty(user.getPassword())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //查询用户
        ApUser dbUser = apUserMapper.selectByApPhone(user.getPhone());
        if(dbUser==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST);
        }

        //选择不同的加密算法实现
        boolean isValid = validateService.validMD5(user, dbUser);
        //        boolean isValid = validateService.validMD5WithSalt(user, dbUser);
        //        boolean isValid = validateService.validDES(user, dbUser);

        if(!isValid){
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }
        //登录处理
        //设置redis
        redisTemplate.opsForValue().set("ap-user-"+user.getId(), JSON.toJSONString(user), USER_EXPIRE);
        //登录成功发送消息
        //kafkaSender.sendUserLoginMessage(user);
        dbUser.setPassword("");
        Map<String,Object> map = Maps.newHashMap();
        map.put("token", AppJwtUtil.getToken(dbUser));
        map.put("user",dbUser);
        return ResponseResult.okResult(map);
    }
}
