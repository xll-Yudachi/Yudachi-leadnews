package com.yudachi.model.article.pojos;

import lombok.Data;

@Data
public class ApArticleLabel {

    public ApArticleLabel(Integer articleId, Integer labelId) {
        this.articleId = articleId;
        this.labelId = labelId;
    }

    public ApArticleLabel() {
    }

    private Integer id;

    private Integer articleId;

    private Integer labelId;

    private Integer count;
}
