package com.yudachi.media.apis;

import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.media.dtos.WmNewsDto;
import com.yudachi.model.media.dtos.WmNewsPageReqDto;

public interface NewsControllerApi {
    /**
     * 提交文章*
     * @param wmNewsDto
     * @return*
     */
    ResponseResult summitNews(WmNewsDto wmNewsDto);

    /**
     * 保存草稿
     * @param wmNewsDto
     * @return
     */
    ResponseResult saveDraftNews(WmNewsDto wmNewsDto);

    /**
     * 根据用户信息分页查询发表的内容
     * @return
     */
    ResponseResult listByUser(WmNewsPageReqDto dto);

    /**
     * 根据id获取文章信息
     * @param wmNewsDto
     * @return
     */
    ResponseResult findWmNewsById(WmNewsDto wmNewsDto);

    /**
     * 删除文章
     * @param wmNews
     * @return
     */
    ResponseResult delNews(WmNewsDto wmNews);
}
