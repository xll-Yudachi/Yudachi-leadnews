package com.yudachi.media.service.impl;

import com.yudachi.common.common.contants.WmMediaConstans;
import com.yudachi.media.service.StatisticsService;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.common.enums.AppHttpCodeEnum;
import com.yudachi.model.mappers.wemedia.WmFansStatisticsMapper;
import com.yudachi.model.mappers.wemedia.WmNewsStatisticsMapper;
import com.yudachi.model.mappers.wemedia.WmUserMapper;
import com.yudachi.model.media.dtos.StatisticDto;
import com.yudachi.model.media.pojos.WmFansStatistics;
import com.yudachi.model.media.pojos.WmUser;
import com.yudachi.utils.common.BurstUtils;
import com.yudachi.utils.threadlocal.WmThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SuppressWarnings("all")
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private WmNewsStatisticsMapper wmNewsStatisticsMapper;
    @Autowired
    private WmUserMapper wmUserMapper;
    @Autowired
    private WmFansStatisticsMapper wmFansStatisticsMapper;

    @Override
    public ResponseResult findWmNewsStatistics(StatisticDto dto) {
        ResponseResult responseResult = check(dto);
        if (responseResult != null){
            return responseResult;
        }
        WmUser wmUser = queryAllUserInfo();
        String burst = BurstUtils.groudOne(wmUser.getApUserId());
        return ResponseResult.okResult(wmNewsStatisticsMapper.findByTimeAndUserId(burst,wmUser.getApUserId(), dto));
    }

    @Override
    public ResponseResult findFansStatistics(StatisticDto dto) {
        ResponseResult responseResult = check(dto);
        if (responseResult != null){
            return responseResult;
        }
        WmUser wmUser = queryAllUserInfo();
        Long userId = wmUser.getApUserId();
        String burst = BurstUtils.groudOne(userId);
        List<WmFansStatistics> datas = wmFansStatisticsMapper.findByTimeAndUserId(burst, userId, dto);
        return ResponseResult.okResult(datas);
    }

    private WmUser queryAllUserInfo() {
        WmUser user = WmThreadLocalUtils.getUser();
        user = wmUserMapper.selectById(user.getId().intValue());
        return user;
    }

    private ResponseResult check(StatisticDto dto) {
        if (dto == null && dto.getType() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        if (WmMediaConstans.WM_NEWS_STATISTIC_CUR != dto.getType() && (dto.getStime() == null || dto.getEtime() == null)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        return null;
    }


}