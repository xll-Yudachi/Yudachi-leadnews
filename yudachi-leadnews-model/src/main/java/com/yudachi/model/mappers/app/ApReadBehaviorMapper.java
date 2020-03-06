package com.yudachi.model.mappers.app;

import com.yudachi.model.behavior.pojos.ApReadBehavior;
import org.apache.ibatis.annotations.Param;

/**
 * @Author Yudachi
 * @Date 2020/2/15 23:33
 * @Version 1.0
 * @Description app阅读行为映射表
 **/
public interface ApReadBehaviorMapper {

    int insert(ApReadBehavior record);

    int update(ApReadBehavior record);

    ApReadBehavior selectByEntryId(@Param("burst") String burst,@Param("entryId") Integer entryId,@Param("articleId") Integer articleId);

}
