package com.yudachi.migration.service;

import com.yudachi.model.article.pojos.ApHotArticles;

import java.util.List;

public interface ApHotArticleService {

    List<ApHotArticles> selectList(ApHotArticles apHotArticlesQuery);

    void insert(ApHotArticles apHotArticles);

    /**
     * 热数据 Hbase 同步
     *
     * @param apArticleId
     */
    public void hotApArticleSync(Integer apArticleId);


    void deleteById(Integer id);

    /**
     * 查询过期的数据
     *
     * @return
     */
    public List<ApHotArticles> selectExpireMonth();


    void deleteHotData(ApHotArticles apHotArticle);
}
