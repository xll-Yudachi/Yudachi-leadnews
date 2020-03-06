package com.yudachi.model.mappers.app;

import com.yudachi.model.user.pojos.ApUserFollow;
import org.apache.ibatis.annotations.Param;

/**
 * @Author Yudachi
 * @Date 2020/2/15 20:09
 * @Version 1.0
 * @Description  用户关注映射表
 **/
public interface ApUserFollowMapper {

    ApUserFollow selectByFollowId(@Param("burst") String burst, @Param("userId") Long userId,@Param("followId") Integer followId);

    int insert(ApUserFollow record);

    int deleteByFollowId(@Param("burst") String burst,@Param("userId") Long userId,@Param("followId") Integer followId);
}
