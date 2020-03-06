package com.yudachi.media.controller.v1;

import com.yudachi.media.apis.StatisticsControllerApi;
import com.yudachi.media.service.StatisticsService;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.media.dtos.StatisticDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController implements StatisticsControllerApi {
    @Autowired
    private StatisticsService statisticsService;

    @Override
    @RequestMapping("/news")
    public ResponseResult newsData(@RequestBody StatisticDto dto) {
        return statisticsService.findWmNewsStatistics(dto);
    }

    @Override
    @RequestMapping("/fans")
    public ResponseResult fansData(@RequestBody StatisticDto dto) {
        return statisticsService.findFansStatistics(dto);
    }
}
