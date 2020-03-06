package com.yudachi.model.mappers.app;

import com.yudachi.model.user.pojos.ApUserFan;
import org.apache.ibatis.annotations.Param;

/**
 * @Author Yudachi
 * @Date 2020/2/15 20:51
 * @Version 1.0
 * @Description 粉丝映射表
 **/
public interface ApUserFanMapper {

    int insert(ApUserFan record);

    ApUserFan selectByFansId(@Param("burst") String burst, @Param("userId") Integer userId ,@Param("fansId") Long fansId);

    int deleteByFansId(@Param("burst") String burst, @Param("userId") Integer userId ,@Param("fansId") Long fansId);
}
