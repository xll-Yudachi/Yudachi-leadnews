package com.yudachi.article.apis;

import com.yudachi.model.article.dtos.UserSearchDto;
import com.yudachi.model.common.dtos.ResponseResult;

/**
 * @Author Yudachi
 * @Date 2020/2/18 20:01
 * @Version 1.0
 * @Description 文章搜索
 **/
public interface ArticleSearchControllerApi {
    /**
     查询搜索历史
     @param userSearchDto
     @return
     */
    ResponseResult findUserSearch(UserSearchDto userSearchDto);

    /**
     删除搜索历史
     @param userSearchDto
     @return
     */
    ResponseResult delUserSearch(UserSearchDto userSearchDto);

    /**
     清空搜索历史
     @param userSearchDto
     @return
     */
    ResponseResult clearUserSearch(UserSearchDto userSearchDto);

    /**
     今日热词
     @return
     */
    ResponseResult hotKeywords(UserSearchDto userSearchDto);

    /**
     联想词
     @param userSearchDto
     @return
     */
    ResponseResult searchAssociate(UserSearchDto userSearchDto);

    /**
     ES文章分页搜索
     @return
     */
    ResponseResult esArticleSearch(UserSearchDto userSearchDto);
}
