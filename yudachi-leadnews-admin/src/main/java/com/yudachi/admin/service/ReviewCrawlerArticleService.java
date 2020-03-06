package com.yudachi.admin.service;

import com.yudachi.model.crawler.pojos.ClNews;

public interface ReviewCrawlerArticleService {
    /**
     * 爬虫端发布文章审核
     */
    public void autoReviewArticleByCrawler(ClNews clNews) throws Exception;

    public void autoReviewArticleByCrawler() throws Exception;

    public void autoReviewArticleByCrawler(Integer clNewsId) throws Exception;
}