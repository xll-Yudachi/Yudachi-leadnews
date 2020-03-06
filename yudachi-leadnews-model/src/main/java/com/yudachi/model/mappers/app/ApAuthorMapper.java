package com.yudachi.model.mappers.app;

import com.yudachi.model.article.pojos.ApAuthor;

import java.util.List;

/**
 * @Author Yudachi
 * @Date 2020/2/15 20:15
 * @Version 1.0
 * @Description 作者信息映射表
 **/
public interface ApAuthorMapper {
    // 查询作者
    ApAuthor selectById(Integer id);

    ApAuthor selectByAuthorName(String authorName);

    void insert(ApAuthor apAuthor);

    List<ApAuthor> selectByIds(List<Integer> ids);
}

