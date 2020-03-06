package com.yudachi.images.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yudachi.common.kafka.messages.app.ApHotArticleMessage;
import com.yudachi.images.service.CacheImageService;
import com.yudachi.images.service.HotArticleImageService;
import com.yudachi.model.article.pojos.ApArticle;
import com.yudachi.model.article.pojos.ApArticleContent;
import com.yudachi.model.article.pojos.ApHotArticles;
import com.yudachi.model.crawler.core.parse.ZipUtils;
import com.yudachi.model.mappers.app.ApArticleContentMapper;
import com.yudachi.model.mappers.app.ApArticleMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@SuppressWarnings("all")
public class HotArticleImageServiceImpl implements HotArticleImageService {


    @Autowired
    private ApArticleContentMapper apArticleContentMapper;
    @Autowired
    private CacheImageService cacheImageService;
    @Autowired
    private ApArticleMapper apArticleMapper;
    @Value("${PREFIX}")
    private String PREFIX;

    @Override
    public void handleHotImage(ApHotArticleMessage message) {
        ApHotArticles hotArticles =  message.getData();
        log.info("处理热文章图片开始#articleId:{},message:{}", hotArticles.getArticleId(), JSON.toJSONString(message));
        ApArticleContent content = apArticleContentMapper.selectByArticleId(hotArticles.getArticleId());
        //文章内容缓存
        String source = ZipUtils.gunzip(content.getContent());
        JSONArray array = JSONArray.parseArray(source);
        for (int i = 0; i< array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if(!"image".equals(obj.getString("type")))
                continue;
            String imagePath = obj.getString("value");
            if(!imagePath.startsWith(PREFIX)){
                log.info("非站内图片不缓存#articleId:{}", hotArticles.getArticleId());
                continue;
            }
            //缓存图片
            cacheImageService.cache2Redis(imagePath, true);
        }
        //主图缓存
        ApArticle article = apArticleMapper.selectById(Long.valueOf(hotArticles.getArticleId()));
        if(StringUtils.isNotEmpty(article.getImages())){
            String[] articleLmages = article.getImages().split(",");
            for (String img : articleLmages){
                if(!img.startsWith(PREFIX)){
                    log.info("非站内图片不缓存#articleId:{}", hotArticles.getArticleId());
                    continue;
                }
                //缓存图片
                cacheImageService.cache2Redis(img, true);
            }
        }
        log.info("处理热文章图片结束#message:{}", JSON.toJSONString(message));
    }
}