package com.yudachi.model.mappers.app;

import com.yudachi.model.article.dtos.ArticleHomeDto;
import com.yudachi.model.article.pojos.ApHotArticles;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApHotArticlesMapper {

    /**
     * 插入热文章数据
     *
     * @param record
     * @return
     */
    int insert(ApHotArticles record);

    /**
     * 移除传入日期之前的文章
     *
     * @param removeDate
     * @return
     */
    int removeHotArticle(String removeDate);

    /**
     * 根据ID删除
     *
     * @param id
     */
    void deleteById(Integer id);

    /**
     * 查询热文章ID
     *
     * @param entryId
     * @param dto
     * @return
     */
    List<ApHotArticles> loadArticleIdListByEntryId(@Param("entryId") Integer entryId, @Param("dto") ArticleHomeDto dto, @Param("type") short type);


    List<ApHotArticles> selectList(ApHotArticles apHotArticles);


    List<ApHotArticles> selectExpireMonth();

    /**
     * 查询今天最大ID
     * @param today
     * @return
     */
    Integer selectTodayMaxScore(String today);

    /**
     * 文章已经阅读
     * @param entryId
     * @param articleId
     * @return
     */
    int updateReadStatus(@Param("entryId") Integer entryId, @Param("articleId") Integer articleId);

    List<ApHotArticles> loadHotListByLocation(@Param("dto") ArticleHomeDto dto, @Param("type") short type);
}