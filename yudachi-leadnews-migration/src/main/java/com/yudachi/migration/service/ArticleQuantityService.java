package com.yudachi.migration.service;

import com.yudachi.migration.entity.ArticleQuantity;

import java.util.List;

public interface ArticleQuantityService {

    /**
     * 获取ArticleQuantity列表
     * @return
     */
    public List<ArticleQuantity> getArticleQuantityList();

    /**
     * 根据ArticleId获取ArticleQuantity
     * @param id
     * @return
     */
    public ArticleQuantity getArticleQuantityByArticleId(Long id);

    /**
     * 根据ByArticleId从Hbase中获取ArticleQuantity
     * @param id
     * @return
     */
    public ArticleQuantity getArticleQuantityByArticleIdForHbase(Long id);

    /**
     * 数据库到Hbase的同步
     */
    public void dbToHbase();

    /**
     * 根据articleId 将数据库的数据同步到Hbase
     * @param articleId
     */
    public void dbToHbase(Integer articleId);


}
