package com.yudachi.media.service;

import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.media.dtos.StatisticDto;

public interface StatisticsService {
    /**
     * 查找图文统计数据
     * @param dto
     * @return
     */
    ResponseResult findWmNewsStatistics(StatisticDto dto);

    /**
     * 用户粉丝统计数据
     * @param dto
     * @return
     */
    ResponseResult findFansStatistics(StatisticDto dto);
}
