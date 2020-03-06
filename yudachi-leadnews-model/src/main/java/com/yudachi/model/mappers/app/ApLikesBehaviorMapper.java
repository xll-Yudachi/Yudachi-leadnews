package com.yudachi.model.mappers.app;

import com.yudachi.model.behavior.pojos.ApLikesBehavior;
import org.apache.ibatis.annotations.Param;

/**
 * @Author Yudachi
 * @Date 2020/2/15 20:11
 * @Version 1.0
 * @Description 点赞行为映射表
 **/
public interface ApLikesBehaviorMapper {

    ApLikesBehavior selectLastLike(@Param("burst") String burst, @Param("objectId") Integer objectId,@Param("entryId") Integer entryId,@Param("type")  Short type);

    int insert(ApLikesBehavior record);
}
