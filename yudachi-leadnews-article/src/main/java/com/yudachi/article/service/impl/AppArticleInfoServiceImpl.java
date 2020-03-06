package com.yudachi.article.service.impl;

import com.google.common.collect.Maps;
import com.yudachi.article.service.AppArticleInfoService;
import com.yudachi.model.article.dtos.ArticleInfoDto;
import com.yudachi.model.article.pojos.*;
import com.yudachi.model.behavior.pojos.ApBehaviorEntry;
import com.yudachi.model.behavior.pojos.ApLikesBehavior;
import com.yudachi.model.behavior.pojos.ApUnlikesBehavior;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.common.enums.AppHttpCodeEnum;
import com.yudachi.model.crawler.core.parse.ZipUtils;
import com.yudachi.model.mappers.app.*;
import com.yudachi.model.user.pojos.ApUser;
import com.yudachi.model.user.pojos.ApUserFollow;
import com.yudachi.utils.common.BurstUtils;
import com.yudachi.utils.threadlocal.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@SuppressWarnings("all")
public class AppArticleInfoServiceImpl implements AppArticleInfoService {

    @Autowired
    private ApArticleMapper apArticleMapper;
    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;
    @Autowired
    private ApArticleContentMapper apArticleContentMapper;
    @Autowired
    private ApBehaviorEntryMapper apBehaviorEntryMapper;
    @Autowired
    private ApUnlikesBehaviorMapper apUnlikesBehaviorMapper;
    @Autowired
    private ApLikesBehaviorMapper apLikesBehaviorMapper;
    @Autowired
    private ApCollectionMapper apCollectionMapper;
    @Autowired
    private ApAuthorMapper apAuthorMapper;
    @Autowired
    private ApUserFollowMapper apUserFollowMapper;

    /**
     * @Description 加载文章详情内容
     * @Params [articleId]
     * @Return com.yudachi.model.common.dtos.ResponseResult
     **/
    @Override
    public ResponseResult getArticleInfo(Integer articleId) {
        // 参数无效
        if (articleId == null || articleId < 1) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApArticleConfig apArticleConfig = apArticleConfigMapper.selectByArticleId(articleId);
        Map<String, Object> data = new HashMap<>();
        // 参数无效
        if (apArticleConfig == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        } else if (!apArticleConfig.getIsDelete()) {
            // 没删除的标识才返回给客户端
            ApArticleContent apArticleContent = apArticleContentMapper.selectByArticleId(articleId);
            // 对编码后的文章内容进行解压操作
            String content = ZipUtils.gunzip(apArticleContent.getContent());
            apArticleContent.setContent(content);
            data.put("content", apArticleContent);
        }
        data.put("config", apArticleConfig);
        ApArticle apArticle = apArticleMapper.selectById(articleId.longValue());
        if (apArticle != null){
            data.put("article", apArticle);
        }
        return ResponseResult.okResult(data);
    }

    /**
     * @Params [dto]
     * @Return com.yudachi.model.common.dtos.ResponseResult
     * @Description 加载文章详情的初始化配置信息，比如关注、喜欢、不喜欢、阅读位置等
     **/
    @Override
    public ResponseResult loadArticleBehavior(ArticleInfoDto dto) {
        ApUser user = AppThreadLocalUtils.getUser();
        // 用户和设备不能同时为空
        if (user == null && dto.getEquipmentId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        Long userId = null;
        if (user != null) {
            userId = user.getId();
        }
        ApBehaviorEntry apBehaviorEntry = apBehaviorEntryMapper.selectByUserIdOrEquipment(userId == null ? null : userId.intValue(), dto.getEquipmentId());
        if (apBehaviorEntry == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        boolean isUnLike=false,isLike=false,isCollection=false,isFollow=false;
        String burst = BurstUtils.groudOne(apBehaviorEntry.getId());

        // 判断是否是已经不喜欢
        ApUnlikesBehavior apUnlikesBehavior = apUnlikesBehaviorMapper.selectLastUnLike(apBehaviorEntry.getId(),dto.getArticleId());
        if(apUnlikesBehavior!=null&&apUnlikesBehavior.getType()==ApUnlikesBehavior.Type.UNLIKE.getCode()){
            isUnLike=true;
        }

        // 判断是否已经喜欢
        ApLikesBehavior apLikesBehavior = apLikesBehaviorMapper.selectLastLike(burst, apBehaviorEntry.getId(), dto.getEquipmentId(), ApCollection.Type.ARTICLE.getCode());
        if(apLikesBehavior!=null&&apLikesBehavior.getOperation()==ApLikesBehavior.Operation.LIKE.getCode()){
            isLike=true;
        }

        // 判断是否收藏
        ApCollection apCollection = apCollectionMapper.selectForEntryId(burst,apBehaviorEntry.getId(),dto.getArticleId(),ApCollection.Type.ARTICLE.getCode());
        if(apCollection!=null){
            isCollection=true;
        }

        // 判断是否关注
        ApAuthor apAuthor = apAuthorMapper.selectById(dto.getAuthorId());
        if(user!=null&&apAuthor!=null&&apAuthor.getUserId()!=null) {
            ApUserFollow apUserFollow = apUserFollowMapper.selectByFollowId(BurstUtils.groudOne(user.getId()), user.getId(), apAuthor.getUserId().intValue());
            if (apUserFollow != null){
                isFollow = true;
            }
        }

        Map<String,Object> data = Maps.newHashMap();
        data.put("isfollow",isFollow);
        data.put("islike",isLike);
        data.put("isunlike",isUnLike);
        data.put("iscollection",isCollection);

        return ResponseResult.okResult(data);


    }
}
