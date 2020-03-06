package com.yudachi.crawler.service;

import com.yudachi.model.crawler.pojos.ClNews;

import java.util.List;

public interface CrawlerNewsService {
    public void saveNews(ClNews clNews);
    public void updateNews(ClNews clNews);
    public void deleteByUrl(String url);
    public List<ClNews> queryList(ClNews clNews);
}
