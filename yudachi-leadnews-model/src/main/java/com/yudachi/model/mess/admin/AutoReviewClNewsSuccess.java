package com.yudachi.model.mess.admin;


import com.yudachi.model.article.pojos.ApArticleConfig;
import com.yudachi.model.article.pojos.ApArticleContent;
import com.yudachi.model.article.pojos.ApAuthor;
import lombok.Data;

@Data
public class AutoReviewClNewsSuccess {
    private ApArticleConfig apArticleConfig;
    private ApArticleContent apArticleContent;
    private ApAuthor apAuthor;

}
