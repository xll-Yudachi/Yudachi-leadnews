package com.yudachi.migration.service;

import com.yudachi.model.article.pojos.ApArticleConfig;

import java.util.List;

public interface ApArticleConfigService {

    List<ApArticleConfig> queryByArticleIds(List<String> ids);

    ApArticleConfig getByArticleId(Integer id);
}
