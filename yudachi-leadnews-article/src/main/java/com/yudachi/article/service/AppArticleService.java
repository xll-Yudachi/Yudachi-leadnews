package com.yudachi.article.service;

import com.yudachi.model.article.dtos.ArticleHomeDto;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.mess.app.ArticleVisitStreamDto;

public interface AppArticleService {
    ResponseResult load(Short type, ArticleHomeDto dto);

    /**
     * 更新阅读数
     * @param dto
     * @return
     */
    ResponseResult updateArticleView(ArticleVisitStreamDto dto);

    /**
     * 加载文章列表数据
     * @param type 1 加载更多  2 加载更新
     * @param dto 封装数据
     * @return 数据列表
     */
    ResponseResult loadV2(Short type, ArticleHomeDto dto, boolean firstPage);
}
