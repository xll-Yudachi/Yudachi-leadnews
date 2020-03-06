package com.yudachi.behavior.apis;

import com.yudachi.model.behavior.dtos.LikesBehaviorDto;
import com.yudachi.model.behavior.dtos.ReadBehaviorDto;
import com.yudachi.model.behavior.dtos.ShowBehaviorDto;
import com.yudachi.model.behavior.dtos.UnLikesBehaviorDto;
import com.yudachi.model.common.dtos.ResponseResult;

/**
 * @Author Yudachi
 * @Description 用户行为接口
 * @Date 2020/2/11 22:21
 * @Version 1.0
 **/
public interface BehaviorControllerApi {

    //APP文章展现行为
    ResponseResult saveShowBehavior(ShowBehaviorDto dto);

    //APP文章点赞行为
    ResponseResult saveLikesBehavior(LikesBehaviorDto dto);

    //APP文章不喜欢行为
    ResponseResult saveUnLikesBehavior( UnLikesBehaviorDto dto) ;

    //APP文章阅读行为
    ResponseResult saveReadBehavior( ReadBehaviorDto dto);
}
