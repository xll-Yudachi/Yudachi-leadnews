package com.yudachi.article.apis;

import com.yudachi.model.article.dtos.ArticleInfoDto;
import com.yudachi.model.common.dtos.ResponseResult;

/**
 * @Author Yudachi
 * @Description 文章信息接口
 * @Date 2020/2/15 19:34
 * @Version 1.0
 **/
public interface ArticleInfoControllerApi {

    /**
     * @Params [dto]
     * @Return com.yudachi.model.common.dtos.ResponseResult
     * @Description 加载文章信息
     **/
    ResponseResult loadArticleInfo(ArticleInfoDto dto);

    /**
     * @Params [dto]
     * @Return com.yudachi.model.common.dtos.ResponseResult
     * @Description 加载文章行为信息
     **/
    ResponseResult loadArticleBehavior( ArticleInfoDto dto);

}
