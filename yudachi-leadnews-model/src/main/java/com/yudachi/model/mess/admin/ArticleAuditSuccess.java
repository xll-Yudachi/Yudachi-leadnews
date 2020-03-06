package com.yudachi.model.mess.admin;

import lombok.Data;

@Data
public class ArticleAuditSuccess {
    // 文章类型
    private ArticleType type;
    private Integer channelId;
    // 文章ID
    private Integer articleId;
    public enum ArticleType {
        WEMEDIA, CRAWLER;
    }
}