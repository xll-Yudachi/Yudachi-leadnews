package com.yudachi.model.mappers.wemedia;

import com.yudachi.model.media.dtos.StatisticDto;
import com.yudachi.model.media.pojos.WmNewsStatistics;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WmNewsStatisticsMapper {

    //根据时间和用户ID查询相关统计数据
    List<WmNewsStatistics> findByTimeAndUserId(@Param("burst") String burst, @Param("userId") Long userId, @Param("dto") StatisticDto dto);

}
