package com.yudachi.model.mappers.app;

import com.yudachi.model.article.dtos.ArticleHomeDto;
import com.yudachi.model.user.pojos.ApUser;
import com.yudachi.model.user.pojos.ApUserArticleList;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApUserArticleListMapper {
    /**
     * 按照用户属性阅读习惯，加载文章id
     * @param user  当前登录的用户
     * @param dto   参数封装对象
     * @param type  加载方向
     * @return
     */
    List<ApUserArticleList> loadArticleIdListByUser(@Param("user") ApUser user, @Param("dto") ArticleHomeDto dto, @Param("type") short type);
}
