package com.yudachi.media.service;

import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.media.dtos.WmNewsDto;
import com.yudachi.model.media.dtos.WmNewsPageReqDto;

public interface NewsService {
    /**
     * 自媒体发布文章
     * @param wmNews
     * @return
     */
    ResponseResult saveNews(WmNewsDto wmNews, Short type);

    /**
     * 查询发布库中当前用户文章信息
     * @param dto
     * @return
     */
    ResponseResult listByUser(WmNewsPageReqDto dto);

    /**
     * 根据文章id查询文章
     * @return
     */
    ResponseResult findWmNewsById(WmNewsDto wmNews);

    /**
     **
     * @param wmNews
     * @return
     */
    ResponseResult delNews(WmNewsDto wmNews);
}