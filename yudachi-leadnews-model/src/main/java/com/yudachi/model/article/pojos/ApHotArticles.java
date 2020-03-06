package com.yudachi.model.article.pojos;

import lombok.Data;

import java.util.Date;

@Data
public class ApHotArticles {
    private Integer id;
    private Integer entryId;
    private Integer tagId;
    private String tagName;
    private Integer score;
    private Integer articleId;
    private Date releaseDate;
    private Date createdTime;
    private Integer provinceId;
    private Integer cityId;
    private Integer countyId;
    private Integer isRead;
}