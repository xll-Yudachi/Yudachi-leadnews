package com.yudachi.article.service;

import com.yudachi.model.article.dtos.UserSearchDto;
import com.yudachi.model.common.dtos.ResponseResult;

public interface ApArticleSearchService {
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
    ResponseResult hotKeywords(String date);

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

    /**
     保存搜索记录
     @param entryId
     @param searchWords
     @return
     */
    ResponseResult saveUserSearch(Integer entryId, String searchWords);

    /**
     * 联想词V2
     * @param userSearchDto
     * @return
     */
    ResponseResult searchAssociateV2(UserSearchDto userSearchDto);
}
