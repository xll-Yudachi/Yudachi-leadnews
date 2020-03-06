package com.yudachi.user.service.impl;

import com.yudachi.common.zookeeper.Sequences;
import com.yudachi.model.article.pojos.ApAuthor;
import com.yudachi.model.behavior.dtos.FollowBehaviorDto;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.common.enums.AppHttpCodeEnum;
import com.yudachi.model.mappers.app.*;
import com.yudachi.model.user.dtos.UserRelationDto;
import com.yudachi.model.user.pojos.ApUser;
import com.yudachi.model.user.pojos.ApUserFan;
import com.yudachi.model.user.pojos.ApUserFollow;
import com.yudachi.user.service.AppFollowBehaviorService;
import com.yudachi.user.service.AppUserRelationService;
import com.yudachi.utils.common.BurstUtils;
import com.yudachi.utils.threadlocal.AppThreadLocalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@SuppressWarnings("all")
public class AppUserRelationServiceImpl implements AppUserRelationService {

    private static Logger logger = LoggerFactory.getLogger(AppUserRelationServiceImpl.class);

    @Autowired
    private ApUserFollowMapper apUserFollowMapper;
    @Autowired
    private ApUserFanMapper apUserFanMapper;
    @Autowired
    private ApAuthorMapper apAuthorMapper;
    @Autowired
    private ApUserMapper apUserMapper;
    @Autowired
    private AppFollowBehaviorService appFollowBehaviorService;
    @Autowired
    private Sequences sequences;
    @Autowired
    private ApBehaviorEntryMapper apBehaviorEntryMapper;

    /**
     * @Author Yudachi
     * @Date 2020/2/15 21:10
     * @Version 1.0
     * @Description 关注/取消关注 操作
     **/
    @Override
    public ResponseResult follow(UserRelationDto dto) {
        if (dto.getOperation() == null || dto.getOperation() < 0 || dto.getOperation() > 1) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "无效的operation参数");
        }
        Integer followId = dto.getUserId();
        if (followId == null && dto.getAuthorId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE, "followId或authorId不能为空");
        } else if (followId == null) {
            ApAuthor apAuthor = apAuthorMapper.selectById(dto.getAuthorId());
            if (apAuthor != null) {
                followId = apAuthor.getUserId().intValue();
            }
        }
        if (followId == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "关注人不存在");
        } else {
            ApUser user = AppThreadLocalUtils.getUser();
            if (user != null) {
                if (dto.getOperation() == 0) {
                    return followByUserId(user, followId, dto.getArticleId());
                } else {
                    return followCancelByUserId(user, followId);
                }
            } else {
                return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            }
        }
    }

    /**
     * @Params [user, followId]
     * @Return com.yudachi.model.common.dtos.ResponseResult
     * @Description 取消关注
     **/
    private ResponseResult followCancelByUserId(ApUser user, Integer followId) {
        int resultNum = 0;

        // 查询关注信息
        ApUserFollow apUserFollow = apUserFollowMapper.selectByFollowId(BurstUtils.groudOne(user.getId()), user.getId(), followId);
        // 没有关注
        if (apUserFollow == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "未关注");
        } else {
            //已关注则取消关注
            ApUserFan fan = apUserFanMapper.selectByFansId(BurstUtils.groudOne(followId), followId, user.getId());
            if (fan != null) {
                // 删除粉丝列表的信息
                apUserFanMapper.deleteByFansId(BurstUtils.groudOne(followId), followId, user.getId());
            }
            // 删除关注列表的信息
            resultNum = apUserFollowMapper.deleteByFollowId(BurstUtils.groudOne(user.getId()), user.getId(), followId);
            return ResponseResult.okResult(resultNum);
        }
    }

    /**
     * @Params [user, followId, articleId]
     * @Return com.yudachi.model.common.dtos.ResponseResult
     * @Description 关注
     **/
    private ResponseResult followByUserId(ApUser user, Integer followId, Integer articleId) {
        int resultNum = 0;
        // 判断用户是否存在
        ApUser followUser = apUserMapper.selectById(followId);
        if(followUser==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"关注用户不存在");
        }
        // 查询关注信息
        ApUserFollow apUserFollow = apUserFollowMapper.selectByFollowId(BurstUtils.groudOne(user.getId()), user.getId(), followId);
        // 没有关注即添加关注信息
        if (apUserFollow == null) {
            // 粉丝表添加粉丝信息
            ApUserFan fan = apUserFanMapper.selectByFansId(BurstUtils.groudOne(followId), followId, user.getId());
            if (fan == null) {
                fan = new ApUserFan();
                fan.setId(sequences.sequenceApUserFan());
                fan.setUserId(followId);
                fan.setFansId(user.getId());
                fan.setFansName(user.getName());
                fan.setLevel((short) 0);
                fan.setIsDisplay(true);
                fan.setIsShieldComment(false);
                fan.setIsShieldLetter(false);
                fan.setBurst(BurstUtils.encrypt(fan.getId(), fan.getUserId()));
                apUserFanMapper.insert(fan);
            }
            // 添加关注信息表
            apUserFollow = new ApUserFollow();
            apUserFollow.setId(sequences.sequenceApUserFollow());
            apUserFollow.setUserId(user.getId());
            apUserFollow.setFollowId(followId);
            apUserFollow.setFollowName(followUser.getName());
            apUserFollow.setCreatedTime(new Date());
            apUserFollow.setLevel((short) 0);
            apUserFollow.setIsNotice(true);
            apUserFollow.setBurst(BurstUtils.encrypt(apUserFollow.getId(), apUserFollow.getUserId()));
            resultNum = apUserFollowMapper.insert(apUserFollow);
            // 记录关注行为表
            FollowBehaviorDto dto = new FollowBehaviorDto();
            dto.setFollowId(followId);
            dto.setArticleId(articleId);
            appFollowBehaviorService.saveFollowBehavior(user.getId(), dto);
            return ResponseResult.okResult(resultNum);
        } else {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST, "已关注");
        }
    }
}
