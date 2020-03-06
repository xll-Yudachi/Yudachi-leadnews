package com.yudachi.model.mappers.app;

import com.yudachi.model.article.pojos.ApArticleConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApArticleConfigMapper {
    ApArticleConfig selectByArticleId(Integer articleId);
    int insert(@Param("apArticleConfig") ApArticleConfig apArticleConfig);
    List<ApArticleConfig> selectByArticleIds(List<String> articleIds);
}
