package com.yudachi.user.apis;

import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.user.dtos.UserRelationDto;

/**
 * @Author Yudachi
 * @Date 2020/2/15 21:31
 * @Version 1.0
 * @Description 用户关注接口
 **/
public interface UserRelationControllerApi {
    ResponseResult follow(UserRelationDto dto);
}
