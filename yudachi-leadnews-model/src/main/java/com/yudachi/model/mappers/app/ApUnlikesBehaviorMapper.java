package com.yudachi.model.mappers.app;

import com.yudachi.model.behavior.pojos.ApUnlikesBehavior;
import org.apache.ibatis.annotations.Param;

/**
 * @Author Yudachi
 * @Date 2020/2/15 20:13
 * @Version 1.0
 * @Description 用户不喜欢行为映射表
 **/
public interface ApUnlikesBehaviorMapper {

    ApUnlikesBehavior selectLastUnLike(@Param("entryId") Integer entryId, @Param("articleId") Integer articleId);

    int insert(ApUnlikesBehavior record);
}
