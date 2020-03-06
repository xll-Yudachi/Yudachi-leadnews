package com.yudachi.migration.service;

import com.yudachi.model.article.pojos.ApArticle;

import java.util.List;

public interface ApArticleService {

    public ApArticle getById(Long id);

    /**
     * 获取未同步的数据
     *
     * @return
     */
    public List<ApArticle> getUnsyncApArticleList();

    /**
     * 更新同步状态
     *
     * @param apArticle
     */
    void updateSyncStatus(ApArticle apArticle);
}