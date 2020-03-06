package com.yudachi.model.mappers.wemedia;

import com.yudachi.model.media.dtos.StatisticDto;
import com.yudachi.model.media.pojos.WmFansStatistics;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WmFansStatisticsMapper {

    //根据时间和用户ID查询相关粉丝数据
    List<WmFansStatistics> findByTimeAndUserId(@Param("burst") String burst, @Param("userId") Long userId, @Param("dto") StatisticDto dto);

}
