package com.yudachi.model.mappers.app;

import com.yudachi.model.article.pojos.ApArticleContent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author Yudachi
 * @Description 文章内容Mapper
 * @Date 2020/2/15 19:20
 * @Version 1.0
 **/
public interface ApArticleContentMapper {
    ApArticleContent selectByArticleId(Integer articleId);
    void insert(@Param("apArticleContent") ApArticleContent apArticleContent);
    List<ApArticleContent> selectByArticleIds(List<String> articleIds);
}
