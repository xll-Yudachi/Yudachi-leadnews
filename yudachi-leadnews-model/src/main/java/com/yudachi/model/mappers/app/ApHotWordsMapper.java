package com.yudachi.model.mappers.app;

import com.yudachi.model.article.pojos.ApHotWords;

import java.util.List;

/**
 * @Author Yudachi
 * @Date 2020/2/18 20:11
 * @Version 1.0
 * @Description 热词关系映射表
 **/
public interface ApHotWordsMapper {
    /**
     查询今日热词
     @param hotDate
     @return
     */
    List<ApHotWords> queryByHotDate(String hotDate);

}
