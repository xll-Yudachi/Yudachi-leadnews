package com.yudachi.migration.entity;

import com.yudachi.common.common.storage.StorageData;
import com.yudachi.common.hbase.entity.HBaseInvok;
import com.yudachi.common.hbase.entity.HBaseStorage;
import com.yudachi.model.article.pojos.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Article 封装数据的工具类
 */
@Data
public class ArticleQuantity {

    /**
     * 文章关系数据实体
     */
    private ApArticle apArticle;

    /**
     * 文章配置实体
     */
    private ApArticleConfig apArticleConfig;
    /**
     * 文章内容实体
     */
    private ApArticleContent apArticleContent;
    /**
     * 文章作者实体
     */
    private ApAuthor apAuthor;
    /**
     * 回调接口
     */
    private HBaseInvok hBaseInvok;


    public Integer getApArticleId() {
        if (null != apArticle) {
            return apArticle.getId();
        }
        return null;
    }

    /**
     * 将ArticleQuantity 对象转换为HBaseStorage对象
     *
     * @return
     */
    public HBaseStorage getHbaseStorage() {
        HBaseStorage hbaseStorage = new HBaseStorage();
        hbaseStorage.setRowKey(String.valueOf(apArticle.getId()));
        hbaseStorage.setHBaseInvok(hBaseInvok);
        StorageData apArticleData = StorageData.getStorageData(apArticle);
        if (null != apArticleData) {
            hbaseStorage.addStorageData(apArticleData);
        }

        StorageData apArticleConfigData = StorageData.getStorageData(apArticleConfig);
        if (null != apArticleConfigData) {
            hbaseStorage.addStorageData(apArticleConfigData);
        }

        StorageData apArticleContentData = StorageData.getStorageData(apArticleContent);
        if (null != apArticleContentData) {
            hbaseStorage.addStorageData(apArticleContentData);
        }

        StorageData apAuthorData = StorageData.getStorageData(apAuthor);
        if (null != apAuthorData) {
            hbaseStorage.addStorageData(apAuthorData);
        }
        return hbaseStorage;
    }


    /**
     * 获取 StorageData 列表
     *
     * @return
     */
    public List<StorageData> getStorageDataList() {
        List<StorageData> storageDataList = new ArrayList<>();
        StorageData apArticleStorageData = StorageData.getStorageData(apArticle);
        if (null != apArticleStorageData) {
            storageDataList.add(apArticleStorageData);
        }

        StorageData apArticleContentStorageData = StorageData.getStorageData(apArticleContent);
        if (null != apArticleContentStorageData) {
            storageDataList.add(apArticleContentStorageData);
        }


        StorageData apArticleConfigStorageData = StorageData.getStorageData(apArticleConfig);
        if (null != apArticleConfigStorageData) {
            storageDataList.add(apArticleConfigStorageData);
        }

        StorageData apAuthorStorageData = StorageData.getStorageData(apAuthor);
        if (null != apAuthorStorageData) {
            storageDataList.add(apAuthorStorageData);
        }
        return storageDataList;
    }


    public ApHotArticles getApHotArticles() {
        ApHotArticles apHotArticles = null;
        if (null != apArticle) {
            apHotArticles = new ApHotArticles();
            apHotArticles.setArticleId(apArticle.getId());
            apHotArticles.setReleaseDate(apArticle.getPublishTime());
            apHotArticles.setScore(1);
            // apHotArticles.setTagId();
            apHotArticles.setTagName(apArticle.getLabels());
            apHotArticles.setCreatedTime(new Date());
        }
        return apHotArticles;

    }

}