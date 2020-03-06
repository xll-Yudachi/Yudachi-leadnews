package com.yudachi.migration.service.impl;

import com.yudachi.migration.service.ApArticleContenService;
import com.yudachi.model.article.pojos.ApArticleContent;
import com.yudachi.model.mappers.app.ApArticleContentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SuppressWarnings("all")
public class ApArticleContenServiceImpl implements ApArticleContenService {

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Override
    public List<ApArticleContent> queryByArticleIds(List<String> ids) {
        return apArticleContentMapper.selectByArticleIds(ids);
    }

    @Override
    public ApArticleContent getByArticleIds(Integer id) {
        return apArticleContentMapper.selectByArticleId(id);
    }
}