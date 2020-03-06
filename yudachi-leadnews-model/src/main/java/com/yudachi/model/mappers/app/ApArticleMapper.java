package com.yudachi.model.mappers.app;


import com.yudachi.model.article.dtos.ArticleHomeDto;
import com.yudachi.model.article.pojos.ApArticle;
import com.yudachi.model.article.pojos.ApArticleSDto;
import com.yudachi.model.user.pojos.ApUserArticleList;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface ApArticleMapper {


    /**
     * 通过ID查询文章
     *
     * @param id
     * @return
     */
    ApArticle selectById(Long id);

    /**
     * 照用户地理位置，加载文章
     *
     * @param dto  参数封装对象
     * @param type 加载方向
     * @return
     */
    List<ApArticle> loadArticleListByLocation(@Param("dto") ArticleHomeDto dto, @Param("type") Short type);

    /**
     * 依据文章IDS来获取文章详细内容
     *
     * @param list 文章ID
     * @return
     */
    List<ApArticle> loadArticleListByIdList(@Param("list") List<ApUserArticleList> list);

    void insert(ApArticle apArticle);

    List<Integer> findByAuthorId(Integer apAuthorId);


    /**
     * 抽取最近的文章数据用于计算热文章
     *
     * @param lastDate
     * @return
     */
    List<ApArticle> loadLastArticleForHot(String lastDate);

    /**
     * 获取当日发布的图文
     * @return
     */
    List<ApArticleSDto> selectListForStatistic();

    /**
     * 更新文章数
     * @param articleId
     * @param viewCount
     * @param collectCount
     * @param commontCount
     * @param likeCount
     * @return
     */
    int updateArticleView(@Param("articleId") Integer articleId, @Param("viewCount") long viewCount,@Param("collectCount") long collectCount,@Param("commontCount")  long commontCount,@Param("likeCount") long likeCount);

    /**
     * 依据文章IDS来获取文章详细内容
     * @param list 文章ID
     * @return
     */
    List<ApArticle> loadArticleListByIdListV2(List<Integer> list);

    /**
     * 查询
     *
     * @param apArticle
     * @return
     */
    List<ApArticle> selectList(ApArticle apArticle);
    /**
     * 更新
     * @param apArticle
     */
    void updateSyncStatus(ApArticle apArticle);
}