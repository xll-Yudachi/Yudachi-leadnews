package com.yudachi.model.crawler.core.cookie;

import com.yudachi.model.crawler.core.proxy.CrawlerProxy;
import lombok.Data;

import java.util.List;

@Data
public class CrawlerHtml {

    public CrawlerHtml() {
    }

    public CrawlerHtml(String url) {
        this.url = url;
    }

    private String url;

    private String html;

    private CrawlerProxy proxy;

    private List<CrawlerCookie> crawlerCookieList = null;

}
