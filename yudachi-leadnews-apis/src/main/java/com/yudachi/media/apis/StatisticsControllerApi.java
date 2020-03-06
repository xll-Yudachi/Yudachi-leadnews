package com.yudachi.media.apis;

import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.media.dtos.StatisticDto;

public interface StatisticsControllerApi {
    /**
     * 文章数据
     * @param dto
     * @return
     */
    public ResponseResult newsData(StatisticDto dto);

    /**
     * 粉丝数据
     * @param dto*
     * @return*
     */
    public ResponseResult fansData(StatisticDto dto);
}
