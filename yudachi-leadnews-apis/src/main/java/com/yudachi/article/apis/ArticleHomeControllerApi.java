package com.yudachi.article.apis;

import com.yudachi.model.article.dtos.ArticleHomeDto;
import com.yudachi.model.common.dtos.ResponseResult;

/**
 * @Author Yudachi
 * @Description 首页文章
 * @Date 2020/2/11 20:01
 * @Version 1.0
 **/
public interface ArticleHomeControllerApi {

    /**
     * @Description 加载首页文章
     * @Params [dto]    封装参数对象
     * @Return com.yudachi.model.common.dtos.ResponseResult
     **/
    ResponseResult load(ArticleHomeDto dto);

    /**
     * @Description 加载更多
     * @Params [dto]    封装参数对象
     * @Return com.yudachi.model.common.dtos.ResponseResult
     **/
    ResponseResult loadMore(ArticleHomeDto dto);

    /**
     * @Description 加载最新数据
     * @Params [dto]    封装参数对象
     * @Return com.yudachi.model.common.dtos.ResponseResult
     **/
    ResponseResult loadNew(ArticleHomeDto dto);
}
