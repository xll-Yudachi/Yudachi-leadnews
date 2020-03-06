package com.yudachi.article.controller.v1;

import com.yudachi.article.apis.ArticleHomeControllerApi;
import com.yudachi.article.service.AppArticleService;
import com.yudachi.common.common.article.constans.ArticleConstans;
import com.yudachi.model.article.dtos.ArticleHomeDto;
import com.yudachi.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/article")
public class ArticleHomeController implements ArticleHomeControllerApi {

    @Autowired
    private AppArticleService appArticleService;

    @Override
    @GetMapping("/load")
    public ResponseResult load(ArticleHomeDto dto) {
        return appArticleService.load(ArticleConstans.LOADTYPE_LOAD_MORE, dto);
    }

    @Override
    @GetMapping("/loadmore")
    public ResponseResult loadMore(ArticleHomeDto dto) {
        return appArticleService.load(ArticleConstans.LOADTYPE_LOAD_MORE, dto);
    }

    @Override
    @GetMapping("/loadnew")
    public ResponseResult loadNew(ArticleHomeDto dto) {
        return appArticleService.load( ArticleConstans.LOADTYPE_LOAD_NEW, dto);
    }
}
