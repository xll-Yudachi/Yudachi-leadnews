package com.yudachi.model.crawler.core.parse.impl;

import com.yudachi.model.crawler.core.parse.ParseItem;
import lombok.Data;

@Data
public class CrawlerParseItem extends ParseItem {

    /**
     * 数据ID
     */
    private String id;
    /**
     * 说明
     */
    private String summary;
    /**
     * 博客url
     */
    private String url;
    /**
     * 个人空间URL
     */

    private String spatialUrl;

    /**
     * 标签
     */
    private String labels;
    /**
     * 策略
     */
    private String strategy;

    /**
     * 标题
     */
    private String title;

    /**
     * 类型
     */
    private String type;
    /**
     * 文档类型
     */
    private int docType;

    /**
     * 副标题
     */
    private String subTitle;

    /**
     * 作者
     */
    private String author;

    /**
     * 发布日期
     */
    private String releaseDate;


    /**
     * 阅读量
     */
    private Integer readCount;

    /**
     * 评论数量
     */
    private Integer commentCount;

    /**
     * 点赞量
     */
    private Integer likes;

    /**
     * 图文内容
     */
    private String content;

    /**
     * 压缩后的内容
     */
    private String compressContent;


    @Override
    public String getInitialUrl() {
        return getUrl();
    }

    @Override
    public String getParserContent() {
        return getContent();
    }

}
