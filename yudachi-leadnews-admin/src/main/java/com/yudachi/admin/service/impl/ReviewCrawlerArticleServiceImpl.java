package com.yudachi.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yudachi.admin.service.ReviewCrawlerArticleService;
import com.yudachi.common.aliyun.AliyunImageScanRequest;
import com.yudachi.common.aliyun.AliyunTextScanRequest;
import com.yudachi.common.common.contants.ESIndexConstants;
import com.yudachi.common.common.pojo.EsIndexEntity;
import com.yudachi.common.kafka.KafkaSender;
import com.yudachi.model.admin.pojos.AdChannel;
import com.yudachi.model.article.pojos.*;
import com.yudachi.model.crawler.pojos.ClNews;
import com.yudachi.model.mappers.admin.AdChannelMapper;
import com.yudachi.model.mappers.app.*;
import com.yudachi.model.mappers.crawerls.ClNewsMapper;
import com.yudachi.model.mess.admin.ArticleAuditSuccess;
import com.yudachi.utils.common.Compute;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Log4j2
@SuppressWarnings("all")
public class ReviewCrawlerArticleServiceImpl implements ReviewCrawlerArticleService {

    @Autowired
    private AliyunTextScanRequest aliyunTextScanRequest;

    @Autowired
    private AliyunImageScanRequest aliyunImageScanRequest;

    @Autowired
    private JestClient jestClient;

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private ApAuthorMapper apAuthorMapper;

    @Autowired
    private AdChannelMapper adChannelMapper;

    @Autowired
    private ClNewsMapper clNewsMapper;

    @Autowired
    private ApArticleLabelMapper apArticleLabelMapper;

    @Autowired
    private KafkaSender kafkaSender;

    @Override
    public void autoReviewArticleByCrawler() throws Exception {
        ClNews param = new ClNews();
        param.setStatus((byte) 1);
        List<ClNews> clNewsList = clNewsMapper.selectList(param);

        if (null != clNewsList && !clNewsList.isEmpty()) {
            log.info("定时任务自动审核检索未审核数量：{}", clNewsList.size());
            for (ClNews clNews : clNewsList) {
                autoReviewArticleByCrawler(clNews);
            }
        } else {
            log.info("定时任务自动审核未检索出数据");
        }
    }

    @Override
    public void autoReviewArticleByCrawler(Integer clNewsId) throws Exception {
        ClNews param = new ClNews();
        param.setId(clNewsId);
        param.setStatus((byte) 1);
        ClNews clNews = clNewsMapper.selectByIdAndStatus(param);
        if (null != clNews) {
            autoReviewArticleByCrawler(clNews);
        }
    }

    @Override
    public void autoReviewArticleByCrawler(ClNews clNews) throws Exception {
        long currentTimeMills = System.currentTimeMillis();
        log.info("开始自动审核流程");
        if (clNews != null){
            // 审核标题和内容匹配度
            String content = clNews.getUnCompressContent();
            String title = clNews.getTitle();
            if (content == null || title == null){
                updateClNews(clNews, "文章内容或标题为空");
                return;
            }
            double degree = Compute.SimilarDegree(content, title);
            if (degree <= 0){
                updateClNews(clNews, "文章和标题不匹配");
                return;
            }
            // 审核图片和文本
            List<String> images = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            JSONArray jsonArray = JSON.parseArray(content);
            handleTextAndImages(images, sb, jsonArray);

            // 文本审核
            String response = aliyunTextScanRequest.textScanRequest(sb.toString());
            if (null == response || !response.equals("pass")){
                updateClNews(clNews, "文本内容审核不通过");
                return;
            }

            // 审核图片
            String imagesResponse = aliyunImageScanRequest.imageScanRequest(images);
            if (null == imagesResponse || !imagesResponse.equals("pass")){
                updateClNews(clNews, "图片内容审核不通过");
                return;
            }

            //保存数据 文章，文章配置，文章内容，作者，标签
            //频道获取
            Integer channelId = clNews.getChannelId();
            String channelName = "";
            if (null != channelId){
                AdChannel adChannel = adChannelMapper.selectByPrimaryKey(channelId);
                if (null != adChannel){
                    channelName = adChannel.getName();
                }
            }
            // 作者
            ApAuthor apAuthor = saveApAuthor(clNews);
            // 文章
            ApArticle apArticle = saveAparticleByCrawler(images, channelId, channelName, apAuthor.getId(), clNews);
            // 保存标签
            saveApArticleLabel(apArticle);

            // 保存文章配置
            ApArticleConfig apArticleConfig = saveAparticleConfig(apArticle);

            // 保存文章内容
            saveAparticleContent(content, apArticle);

            // 创建索引
            log.info("开始创建索引");
            //6.创建索引
            try {
                createEsIndex(apArticle, content, title, channelId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 修改状态
            //修改状态为审核通过待发布
            log.info("更新原始文章状态为待发布");
            updateClNewsSuccess(clNews);

            //文章审核成功
            ArticleAuditSuccess articleAuditSuccess = new ArticleAuditSuccess();
            articleAuditSuccess.setArticleId(apArticle.getId());
            articleAuditSuccess.setType(ArticleAuditSuccess.ArticleType.CRAWLER);
            articleAuditSuccess.setChannelId(apArticle.getChannelId());

            kafkaSender.sendArticleAuditSuccessMessage(articleAuditSuccess);

        }
        log.info("审核流程结束，耗时：{}", System.currentTimeMillis() - currentTimeMills);
    }

    private void updateClNewsSuccess(ClNews clNews) {
        clNews.setStatus((byte) 9);
        clNewsMapper.updateStatus(clNews);
    }

    private void createEsIndex(ApArticle apArticle, String content, String title, Integer channelId) throws IOException {
        EsIndexEntity esIndexEntity = saveEsIndexEntityByCrawler(content, title, channelId, apArticle);
        Index.Builder builder = new Index.Builder(esIndexEntity);
        builder.id(apArticle.getId().toString());
        builder.refresh(true);
        Index index = builder.index(ESIndexConstants.ARTICLE_INDEX).type(ESIndexConstants.DEFAULT_DOC).build();
        JestResult result = jestClient.execute(index);
        if (result != null && !result.isSucceeded()) {
            throw new RuntimeException(result.getErrorMessage() + "插入更新索引失败!");
        }
    }

    private EsIndexEntity saveEsIndexEntityByCrawler(String content, String title, Integer channelId, ApArticle apArticle) {
        EsIndexEntity esIndexEntity = new EsIndexEntity();
        esIndexEntity.setId(new Long(apArticle.getId()));
        if (null != channelId) {
            esIndexEntity.setChannelId(new Long(channelId));
        }
        esIndexEntity.setContent(content);
        esIndexEntity.setPublishTime(new Date());
        esIndexEntity.setStatus(new Long(1));
        esIndexEntity.setTitle(title);
        esIndexEntity.setTag("article");
        return esIndexEntity;
    }

    private void saveAparticleContent(String content, ApArticle apArticle) {
        ApArticleContent apArticleContent = new ApArticleContent();
        apArticleContent.setArticleId(apArticle.getId());
        apArticleContent.setContent(content);
        apArticleContentMapper.insert(apArticleContent);
    }

    private ApArticleConfig saveAparticleConfig(ApArticle apArticle) {
        ApArticleConfig apArticleConfig = new ApArticleConfig();
        apArticleConfig.setArticleId(apArticle.getId());
        apArticleConfig.setIsComment(true);
        apArticleConfig.setIsDelete(false);
        apArticleConfig.setIsDown(false);
        apArticleConfig.setIsForward(true);
        apArticleConfigMapper.insert(apArticleConfig);
        return apArticleConfig;
    }

    private void saveApArticleLabel(ApArticle apArticle) {
        if (null != apArticle && StringUtils.isNotEmpty(apArticle.getLabels())) {
            String[] labelIdArray = apArticle.getLabels().split(",");
            for (String labelId : labelIdArray) {
                ApArticleLabel tmp = new ApArticleLabel(apArticle.getId(), Integer.parseInt(labelId));
                List<ApArticleLabel> apArticleLabelList = apArticleLabelMapper.selectList(tmp);
                if (null != apArticleLabelList && !apArticleLabelList.isEmpty()) {
                    ApArticleLabel apArticleLabel = apArticleLabelList.get(0);
                    apArticleLabel.setCount(apArticleLabel.getCount() + 1);
                    apArticleLabelMapper.updateByPrimaryKeySelective(apArticleLabel);
                } else {
                    tmp.setCount(1);
                    apArticleLabelMapper.insertSelective(tmp);
                }
            }
        }
    }

    private ApArticle saveAparticleByCrawler(List<String> images, Integer channelId, String channelName, Integer authorId, ClNews clNews) {
        ApArticle apArticle = new ApArticle();
        apArticle.setChannelId(channelId);
        apArticle.setChannelName(channelName);
        apArticle.setAuthorId(new Long(authorId));
        apArticle.setAuthorName(clNews.getName());
        apArticle.setLayout((short)clNews.getType());
        apArticle.setPublishTime(clNews.getPublishTime());
        apArticle.setTitle(clNews.getTitle());
        apArticle.setOrigin(false);
        apArticle.setCreatedTime(new Date());
        StringBuilder sb = new StringBuilder();
        Short layout = 0;
        if (images != null && !images.isEmpty()){
            for (int i = 0; i < images.size() && i < 3; i++) {
                if (i != 0) {
                    sb.append(",");
                }
                layout++;
                sb.append(images.get(i));
            }
        }
        apArticle.setImages(sb.toString());
        apArticleMapper.insert(apArticle);
        return apArticle;
    }

    private ApAuthor saveApAuthor(ClNews clNews) {
        ApAuthor apAuthor = apAuthorMapper.selectByAuthorName(clNews.getName());
        if (apAuthor == null || apAuthor.getId() == null){
            apAuthor = new ApAuthor();
            apAuthor.setCreatedTime(new Date());
            apAuthor.setName(clNews.getName());
            apAuthor.setType(0);
            apAuthorMapper.insert(apAuthor);
        }
        return apAuthor;

    }

    private void handleTextAndImages(List<String> images, StringBuilder sb, JSONArray jsonArray) {
        for (Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;
            String type = (String) jsonObj.get("type");
            if ("image".equals(type)) {
                String value = (String) jsonObj.get("value");
                images.add(value);
            }
            if ("text".equals(type)) {
                sb.append(jsonObj.get("value"));
            }
        }
    }

    private void updateClNews(ClNews clNews, String message) {
    }


}
