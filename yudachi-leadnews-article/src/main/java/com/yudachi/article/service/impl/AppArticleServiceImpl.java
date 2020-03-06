package com.yudachi.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.yudachi.article.service.AppArticleService;
import com.yudachi.common.common.article.constans.ArticleConstans;
import com.yudachi.model.article.dtos.ArticleHomeDto;
import com.yudachi.model.article.pojos.ApArticle;
import com.yudachi.model.article.pojos.ApHotArticles;
import com.yudachi.model.behavior.pojos.ApBehaviorEntry;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.mappers.app.ApArticleMapper;
import com.yudachi.model.mappers.app.ApBehaviorEntryMapper;
import com.yudachi.model.mappers.app.ApHotArticlesMapper;
import com.yudachi.model.mappers.app.ApUserArticleListMapper;
import com.yudachi.model.mess.app.ArticleVisitStreamDto;
import com.yudachi.model.user.pojos.ApUser;
import com.yudachi.model.user.pojos.ApUserArticleList;
import com.yudachi.utils.threadlocal.AppThreadLocalUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("all")
@Service
@Log4j2
public class AppArticleServiceImpl implements AppArticleService {

    // 单页最大加载的数字
    private final static short MAX_PAGE_SIZE = 50;

    @Autowired
    private ApArticleMapper apArticleMapper;
    @Autowired
    private ApUserArticleListMapper apUserArticleListMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ApBehaviorEntryMapper apBehaviorEntryMapper;
    @Autowired
    private ApHotArticlesMapper apHotArticlesMapper;

    /**
     * @Description 加载数据
     * @Params type 1 加载更多  2 加载更新
     * dto 封装数据
     * @Return com.yudachi.model.common.dtos.ResponseResult
     **/
    @Override
    public ResponseResult load(Short type, ArticleHomeDto dto) {
        ApUser user = AppThreadLocalUtils.getUser();
        Integer size = dto.getSize();
        String tag = dto.getTag();
        // 分页参数校验
        if (size == null || size <= 0) {
            size = 20;
        }
        size = Math.min(size, MAX_PAGE_SIZE);

        dto.setSize(size);

        // 类型参数校验
        if (!type.equals(ArticleConstans.LOADTYPE_LOAD_MORE) && !type.equals(ArticleConstans.LOADTYPE_LOAD_NEW)) {
            type = ArticleConstans.LOADTYPE_LOAD_MORE;
        }

        // 文章频道参数验证
        if (StringUtils.isEmpty(tag)) {
            dto.setTag(ArticleConstans.DEFAULT_TAG);
        }

        // 最大时间处理
        if (dto.getMaxBehotTime() == null) {
            dto.setMaxBehotTime(new Date());
        }

        // 最小时间处理
        if (dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date());
        }

        // 数据加载
        // 用户存在 加载推荐内容
        if (user != null) {
            return ResponseResult.okResult(getUserArticle(user, dto, type));
        } else {
            //用户不存在 加载默认内容
            return ResponseResult.okResult(getDefaultArticle(dto, type));
        }
    }

    @Override
    public ResponseResult updateArticleView(ArticleVisitStreamDto dto) {
        int rows = apArticleMapper.updateArticleView(dto.getArticleId(),
                dto.getView(), dto.getCollect(), dto.getCommont(), dto.getLike());
        log.info("更新文章阅读数#articleId：{},dto：{}", dto.getArticleId(), JSON.toJSONString(dto), rows);
        return ResponseResult.okResult(rows);
    }

    /**
     * 从默认的大文章列表中获取文章
     *
     * @param dto
     * @param type
     * @return
     */
    private Object getDefaultArticle(ArticleHomeDto dto, Short type) {
        return apArticleMapper.loadArticleListByLocation(dto, type);
    }


    /**
     * 先从用户的推荐表中查找文章，如果没有再从大文章列表中获取
     *
     * @param user
     * @param dto
     * @param type
     * @return
     */
    private Object getUserArticle(ApUser user, ArticleHomeDto dto, Short type) {
        List<ApUserArticleList> list = apUserArticleListMapper.loadArticleIdListByUser(user, dto, type);
        System.err.println(list.size());
        if (!list.isEmpty()) {
            List<ApArticle> tmp = apArticleMapper.loadArticleListByIdList(list);
            return tmp;
        } else {
            return getDefaultArticle(dto, type);
        }
    }


    @Override
    public ResponseResult loadV2(Short type, ArticleHomeDto dto, boolean firstPage) {
        if (null == dto) {
            dto = new ArticleHomeDto();
        }
        ApUser user = AppThreadLocalUtils.getUser();
        Integer size = dto.getSize();
        String tag = dto.getTag();
        // 分页参数校验
        if (size == null || size <= 0) {
            size = 20;
        }
        size = Math.min(size, MAX_PAGE_SIZE);
        dto.setSize(size);
        //  类型参数校验
        if (!type.equals(ArticleConstans.LOADTYPE_LOAD_MORE) && !type.equals(ArticleConstans.LOADTYPE_LOAD_NEW))
            type = ArticleConstans.LOADTYPE_LOAD_MORE;
        // 文章频道参数验证
        if (StringUtils.isEmpty(tag)) {
            dto.setTag(ArticleConstans.DEFAULT_TAG);
        }
        // 最大时间处理
        if (dto.getMaxBehotTime() == null) {
            dto.setMaxBehotTime(new Date());
        }
        // 最小时间处理
        if (dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date());
        }
        //从缓存中读取 否则数据库查询
        if (firstPage) {
            // 数据加载
            List<ApArticle> cacheList = getCacheArticleV2(dto);
            if (cacheList.size() > 0) {
                log.info("使用缓存加载数据#tag:{}", dto.getTag());
                return ResponseResult.okResult(getCacheArticleV2(dto));
            }
        }
        // 数据加载
        if (user != null) {
            return ResponseResult.okResult(getUserArticleV2(user, dto, type));
        } else {
            return ResponseResult.okResult(getDefaultArticleV2(dto, type));
        }
    }

    /**
     * 查询缓存首页文章数据
     *
     * @param dto
     * @return
     */
    private List<ApArticle> getCacheArticleV2(ArticleHomeDto dto) {
        log.info("查询缓存热文章数据#tag:{}", dto.getTag());
        String key = ArticleConstans.HOT_ARTICLE_FIRST_PAGE + dto.getTag();
        String ret = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(ret)) {
            return Lists.newArrayList();
        }
        List<ApArticle> list = JSONArray.parseArray(ret, ApArticle.class);
        log.info("查询缓存热文章数据#tag:{}, size:{}", dto.getTag(), list.size());
        return list;
    }

    /**
     * 先从用户的推荐表中查找文章，如果没有再从大文章列表中获取
     *
     * @param user
     * @param dto
     * @param type
     * @return
     */
    private List<ApArticle> getUserArticleV2(ApUser user, ArticleHomeDto dto, Short type) {
        // 用户和设备不能同时为空
        if (user == null) {
            return Lists.newArrayList();
        }
        Long userId = user.getId();
        ApBehaviorEntry apBehaviorEntry = apBehaviorEntryMapper.selectByUserIdOrEquipment(userId == null ? null : userId.intValue(), null);
        // 行为实体找以及注册了，逻辑上这里是必定有值得，除非参数错误
        if (apBehaviorEntry == null) {
            return Lists.newArrayList();
        }
        Integer entryId = apBehaviorEntry.getId();
        //如果没查到 查询全局热文章
        if (entryId == null) entryId = 0;
        long time = System.currentTimeMillis();
        List<ApHotArticles> list = apHotArticlesMapper.loadArticleIdListByEntryId(entryId, dto, type);
        System.out.println("==================1=:" + (System.currentTimeMillis() - time));
        //默认从热文章里查询
        if (!list.isEmpty()) {
            List<Integer> articleList = list.stream().map(p -> p.getArticleId()).collect(Collectors.toList());
            List<ApArticle> temp = apArticleMapper.loadArticleListByIdListV2(articleList);
            System.out.println("==================2=:" + (System.currentTimeMillis() - time));
            return temp;
        } else {
            return getDefaultArticleV2(dto, type);
        }
    }

    /**
     * 从默认的热数据列表中获取文章
     *
     * @param dto
     * @param type
     * @return
     */
    private List<ApArticle> getDefaultArticleV2(ArticleHomeDto dto, Short type) {
        List<ApHotArticles> hotList = apHotArticlesMapper.loadHotListByLocation(dto, type);
        List<ApArticle> articleList = Lists.newArrayList();
        for (ApHotArticles hot : hotList) {
            ApArticle article = apArticleMapper.selectById(Long.valueOf(hot.getArticleId()));
            articleList.add(article);
        }
        return articleList;
    }
}
