package com.yudachi.migration.service.impl;

import com.yudachi.common.common.contants.HBaseConstants;
import com.yudachi.common.common.storage.StorageData;
import com.yudachi.common.hbase.HBaseStorageClient;
import com.yudachi.common.mongo.entity.MongoStorageEntity;
import com.yudachi.migration.entity.ArticleQuantity;
import com.yudachi.migration.service.ApHotArticleService;
import com.yudachi.migration.service.ArticleQuantityService;
import com.yudachi.model.article.pojos.ApHotArticles;
import com.yudachi.model.mappers.app.ApHotArticlesMapper;
import com.yudachi.utils.common.DataConvertUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 热点数据操作Service 类
 */
@Service
@Log4j2
@SuppressWarnings("all")
public class ApHotArticleServiceImpl implements ApHotArticleService {

    @Autowired
    private ApHotArticlesMapper apHotArticlesMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ArticleQuantityService articleQuantityService;

    @Autowired
    private HBaseStorageClient hBaseStorageClient;

    @Override
    public List<ApHotArticles> selectList(ApHotArticles apHotArticlesQuery) {
        return apHotArticlesMapper.selectList(apHotArticlesQuery);
    }

    /**
     * 根据ID删除
     *
     * @param id
     */
    @Override
    public void deleteById(Integer id) {
        log.info("删除热数据，apArticleId：{}", id);
        apHotArticlesMapper.deleteById(id);
    }

    /**
     * 查询一个月之前的数据
     *
     * @return
     */
    @Override
    public List<ApHotArticles> selectExpireMonth() {
        return apHotArticlesMapper.selectExpireMonth();
    }

    /**
     * 删除过去的热数据
     *
     * @param apHotArticle
     */
    @Override
    public void deleteHotData(ApHotArticles apHotArticle) {
        deleteById(apHotArticle.getId());
        String rowKey = DataConvertUtils.toString(apHotArticle.getId());
        hBaseStorageClient.gethBaseClent().deleteRow(HBaseConstants.APARTICLE_QUANTITY_TABLE_NAME, rowKey);
        MongoStorageEntity mongoStorageEntity = mongoTemplate.findById(rowKey, MongoStorageEntity.class);
        if (null != mongoStorageEntity) {
            mongoTemplate.remove(mongoStorageEntity);
        }
    }

    /**
     * 插入操作
     *
     * @param apHotArticles
     */
    @Override
    public void insert(ApHotArticles apHotArticles) {
        apHotArticlesMapper.insert(apHotArticles);
    }

    /**
     * 热点数据同步方法
     *
     * @param apArticleId
     */
    @Override
    public void hotApArticleSync(Integer apArticleId) {
        log.info("开始将热数据同步，apArticleId：{}", apArticleId);
        ArticleQuantity articleQuantity = getHotArticleQuantity(apArticleId);
        if (null != articleQuantity) {
            //热点数据同步到DB中
            hotApArticleToDBSync(articleQuantity);
            //热点数据同步到MONGO
            hotApArticleMongoSync(articleQuantity);
            log.info("热数据同步完成，apArticleId：{}", apArticleId);
        } else {
            log.error("找不到对应的热数据，apArticleId：{}", apArticleId);
        }
    }

    /**
     * 获取热数据的ArticleQuantity 对象
     *
     * @param apArticleId
     * @return
     */
    private ArticleQuantity getHotArticleQuantity(Integer apArticleId) {
        Long id = Long.valueOf(apArticleId);
        ArticleQuantity articleQuantity = articleQuantityService.getArticleQuantityByArticleId(id);
        if (null == articleQuantity) {
            articleQuantity = articleQuantityService.getArticleQuantityByArticleIdForHbase(id);
        }
        return articleQuantity;
    }


    /**
     * 热数据 到数据库Mysql的同步
     *
     * @param articleQuantity
     */
    public void hotApArticleToDBSync(ArticleQuantity articleQuantity) {
        Integer apArticleId = articleQuantity.getApArticleId();
        log.info("开始将热数据从Hbase同步到mysql，apArticleId：{}", apArticleId);
        if (null == apArticleId) {
            log.error("apArticleId不存在无法进行同步");
            return;
        }
        ApHotArticles apHotArticlesQuery = new ApHotArticles() {{
            setArticleId(apArticleId);
        }};
        List<ApHotArticles> apHotArticlesList = apHotArticlesMapper.selectList(apHotArticlesQuery);
        if (null != apHotArticlesList && !apHotArticlesList.isEmpty()) {
            log.info("Mysql数据已同步过不需要再次同步,apArticleId:{}", apArticleId);
        } else {
            ApHotArticles apHotArticles = articleQuantity.getApHotArticles();
            apHotArticlesMapper.insert(apHotArticles);
        }
        log.info("将热数据从Hbase同步到mysql完成，apArticleId：{}", apArticleId);
    }

    /**
     * 热数据向从Hbase到Mongodb同步
     *
     * @param articleQuantity
     */
    public void hotApArticleMongoSync(ArticleQuantity articleQuantity) {
        Integer apArticleId = articleQuantity.getApArticleId();
        log.info("开始将热数据从Hbase同步到MongoDB，apArticleId：{}", apArticleId);
        if (null == apArticleId) {
            log.error("apArticleId不存在无法进行同步");
            return;
        }
        String rowKeyId = DataConvertUtils.toString(apArticleId);
        MongoStorageEntity mongoStorageEntity = mongoTemplate.findById(rowKeyId, MongoStorageEntity.class);
        if (null != mongoStorageEntity) {
            log.info("MongoDB数据已同步过不需要再次同步,apArticleId:{}", apArticleId);
        } else {
            List<StorageData> storageDataList = articleQuantity.getStorageDataList();
            if (null != storageDataList && !storageDataList.isEmpty()) {
                mongoStorageEntity = new MongoStorageEntity();
                mongoStorageEntity.setDataList(storageDataList);
                mongoStorageEntity.setRowKey(rowKeyId);
                mongoTemplate.insert(mongoStorageEntity);
            }

        }
        log.info("将热数据从Hbase同步到MongoDB完成，apArticleId：{}", apArticleId);
    }
}