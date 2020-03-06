package com.yudachi.model.mappers.app;

import com.yudachi.model.article.pojos.ApCollection;
import org.apache.ibatis.annotations.Param;

/**
 * @Author Yudachi
 * @Date 2020/2/15 20:04
 * @Version 1.0
 * @Description  文章收藏表映射
 **/
public interface ApCollectionMapper {
    ApCollection selectForEntryId(@Param("burst") String burst, @Param("objectId")Integer objectId, @Param("entryId")Integer entryId, @Param("type")Short type);
}
