package com.yudachi.migration.service.impl;

import com.yudachi.common.common.contants.HBaseConstants;
import com.yudachi.common.hbase.HBaseStorageClient;
import com.yudachi.common.hbase.entity.HBaseStorage;
import com.yudachi.migration.entity.ArticleHBaseInvok;
import com.yudachi.migration.entity.ArticleQuantity;
import com.yudachi.migration.service.*;
import com.yudachi.model.article.pojos.ApArticle;
import com.yudachi.model.article.pojos.ApArticleConfig;
import com.yudachi.model.article.pojos.ApArticleContent;
import com.yudachi.model.article.pojos.ApAuthor;
import com.yudachi.utils.common.DataConvertUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 查询未同步的数据，并封装成ArticleQuantity 对象
 */
@Service
@Log4j2
public class ArticleQuantityServiceImpl implements ArticleQuantityService {

    @Autowired
    private ApArticleContenService apArticleContenService;
    @Autowired
    private ApArticleConfigService apArticleConfigService;
    @Autowired
    private ApAuthorService apAuthorService;

    @Autowired
    private HBaseStorageClient hBaseStorageClient;


    @Autowired
    private ApArticleService apArticleService;

    /**
     * 查询位同步数据的列表
     *
     * @return
     */
    public List<ArticleQuantity> getArticleQuantityList() {
        log.info("生成ArticleQuantity列表");
        //查询未同步的数据
        List<ApArticle> apArticleList = apArticleService.getUnsyncApArticleList();
        if (apArticleList.isEmpty()) {
            return null;
        }
        //获取ArticleId 的list
        List<String> apArticleIdList = apArticleList.stream().map(apArticle -> String.valueOf(apArticle.getId())).collect(Collectors.toList());
        //获取AuthorId 的 list
        List<Integer> apAuthorIdList = apArticleList.stream().map(apAuthor -> apAuthor.getAuthorId() == null ? null : apAuthor.getAuthorId().intValue()).filter(x -> x != null).collect(Collectors.toList());
        //根据apArticleIdList 批量查询出内容列表
        List<ApArticleContent> apArticleContentList = apArticleContenService.queryByArticleIds(apArticleIdList);
        //根据apArticleIdList 批量查询出配置列表
        List<ApArticleConfig> apArticleConfigList = apArticleConfigService.queryByArticleIds(apArticleIdList);
        //根据apAuthorIdList 批量查询出作者列
        List<ApAuthor> apAuthorList = apAuthorService.queryByIds(apAuthorIdList);

        //将不同的对象转换为 ArticleQuantity 对象
        List<ArticleQuantity> articleQuantityList = apArticleList.stream().map(apArticle -> {
            return new ArticleQuantity() {{
                //设置apArticle 对象
                setApArticle(apArticle);
                // 根据apArticle.getId() 过滤出符合要求的 ApArticleContent 对象
                List<ApArticleContent> apArticleContents = apArticleContentList.stream().filter(x -> x.getArticleId().equals(apArticle.getId())).collect(Collectors.toList());
                if (null != apArticleContents && !apArticleContents.isEmpty()) {
                    setApArticleContent(apArticleContents.get(0));
                }
                // 根据 apArticle.getId 过滤出 ApArticleConfig 对象
                List<ApArticleConfig> apArticleConfigs = apArticleConfigList.stream().filter(x -> x.getArticleId().equals(apArticle.getId())).collect(Collectors.toList());
                if (null != apArticleConfigs && !apArticleConfigs.isEmpty()) {
                    setApArticleConfig(apArticleConfigs.get(0));
                }
                // 根据 apArticle.getAuthorId().intValue() 过滤出 ApAuthor 对象
                List<ApAuthor> apAuthors = apAuthorList.stream().filter(x -> x.getId().equals(apArticle.getAuthorId().intValue())).collect(Collectors.toList());
                if (null != apAuthors && !apAuthors.isEmpty()) {
                    setApAuthor(apAuthors.get(0));
                }
                //设置回调方法 用户方法的回调 用于修改同步状态 插入Hbase 成功后同步状态改为已同步
                setHBaseInvok(new ArticleHBaseInvok(apArticle, (x) -> apArticleService.updateSyncStatus(x)));
            }};
        }).collect(Collectors.toList());
        if (null != articleQuantityList && !articleQuantityList.isEmpty()) {
            log.info("生成ArticleQuantity列表完成，size:{}", articleQuantityList.size());
        } else {
            log.info("生成ArticleQuantity列表完成，size:{}", 0);
        }

        return articleQuantityList;
    }


    public ArticleQuantity getArticleQuantityByArticleId(Long id) {
        if (null == id) {
            return null;
        }
        ArticleQuantity articleQuantity = null;
        ApArticle apArticle = apArticleService.getById(id);
        if (null != apArticle) {
            articleQuantity = new ArticleQuantity();
            articleQuantity.setApArticle(apArticle);
            ApArticleContent apArticleContent = apArticleContenService.getByArticleIds(id.intValue());
            articleQuantity.setApArticleContent(apArticleContent);
            ApArticleConfig apArticleConfig = apArticleConfigService.getByArticleId(id.intValue());
            articleQuantity.setApArticleConfig(apArticleConfig);
            ApAuthor apAuthor = apAuthorService.getById(apArticle.getAuthorId());
            articleQuantity.setApAuthor(apAuthor);
        }
        return articleQuantity;
    }


    public ArticleQuantity getArticleQuantityByArticleIdForHbase(Long id) {
        if (null == id) {
            return null;
        }
        ArticleQuantity articleQuantity = null;
        List<Class> typeList = Arrays.asList(ApArticle.class, ApArticleContent.class, ApArticleConfig.class, ApAuthor.class);
        List<Object> objectList = hBaseStorageClient.getStorageDataEntityList(HBaseConstants.APARTICLE_QUANTITY_TABLE_NAME, DataConvertUtils.toString(id), typeList);
        if (null != objectList && !objectList.isEmpty()) {
            articleQuantity = new ArticleQuantity();
            for (Object value : objectList) {
                if (value instanceof ApArticle) {
                    articleQuantity.setApArticle((ApArticle) value);
                } else if (value instanceof ApArticleContent) {
                    articleQuantity.setApArticleContent((ApArticleContent) value);
                } else if (value instanceof ApArticleConfig) {
                    articleQuantity.setApArticleConfig((ApArticleConfig) value);
                } else if (value instanceof ApAuthor) {
                    articleQuantity.setApAuthor((ApAuthor) value);
                }
            }
        }
        return articleQuantity;
    }

    /**
     * 数据库到Hbase同步
     */
    public void dbToHbase() {
        long cutrrentTime = System.currentTimeMillis();
        List<ArticleQuantity> articleQuantitList = getArticleQuantityList();
        if (null != articleQuantitList && !articleQuantitList.isEmpty()) {
            log.info("开始进行定时数据库到HBASE同步，筛选出未同步数据量：{}", articleQuantitList.size());
            if (null != articleQuantitList && !articleQuantitList.isEmpty()) {
                List<HBaseStorage> hbaseStorageList = articleQuantitList.stream().map(ArticleQuantity::getHbaseStorage).collect(Collectors.toList());
                hBaseStorageClient.addHBaseStorage(HBaseConstants.APARTICLE_QUANTITY_TABLE_NAME, hbaseStorageList);
            }
        } else {
            log.info("定时数据库到HBASE同步为筛选出数据");
        }

        log.info("定时数据库到HBASE同步结束，耗时:{}", System.currentTimeMillis() - cutrrentTime);
    }

    @Override
    public void dbToHbase(Integer articleId) {
        long cutrrentTime = System.currentTimeMillis();
        log.info("开始进行异步数据库到HBASE同步，articleId：{}", articleId);
        if (null != articleId) {
            ArticleQuantity articleQuantity = getArticleQuantityByArticleId(articleId.longValue());
            if (null != articleQuantity) {
                HBaseStorage hBaseStorage = articleQuantity.getHbaseStorage();
                hBaseStorageClient.addHBaseStorage(HBaseConstants.APARTICLE_QUANTITY_TABLE_NAME, hBaseStorage);
            }
        }
        log.info("异步数据库到HBASE同步结束，articleId：{}，耗时:{}", articleId, System.currentTimeMillis() - cutrrentTime);
    }
}