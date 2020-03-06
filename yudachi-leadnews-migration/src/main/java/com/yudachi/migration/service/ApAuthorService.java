package com.yudachi.migration.service;

import com.yudachi.model.article.pojos.ApAuthor;

import java.util.List;

public interface ApAuthorService {

    List<ApAuthor> queryByIds(List<Integer> ids);

    ApAuthor getById(Long id);
}