package com.yudachi.migration.service.impl;

import com.yudachi.migration.service.ApAuthorService;
import com.yudachi.model.article.pojos.ApAuthor;
import com.yudachi.model.mappers.app.ApAuthorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SuppressWarnings("all")
public class ApAuthorServiceImpl implements ApAuthorService {

    @Autowired
    private ApAuthorMapper apAuthorMapper;

    @Override
    public List<ApAuthor> queryByIds(List<Integer> ids) {
        return apAuthorMapper.selectByIds(ids);
    }

    @Override
    public ApAuthor getById(Long id) {
        if (null != id) {
            return apAuthorMapper.selectById(id.intValue());
        }
        return null;

    }
}