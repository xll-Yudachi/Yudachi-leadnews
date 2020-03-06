package com.yudachi.behavior.controller.v1;

import com.yudachi.behavior.apis.BehaviorControllerApi;
import com.yudachi.behavior.service.AppLikesBehaviorService;
import com.yudachi.behavior.service.AppReadBehaviorService;
import com.yudachi.behavior.service.AppShowBehaviorService;
import com.yudachi.behavior.service.AppUnLikesBehaviorService;
import com.yudachi.model.behavior.dtos.LikesBehaviorDto;
import com.yudachi.model.behavior.dtos.ReadBehaviorDto;
import com.yudachi.model.behavior.dtos.ShowBehaviorDto;
import com.yudachi.model.behavior.dtos.UnLikesBehaviorDto;
import com.yudachi.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/behavior")
public class BehaviorController implements BehaviorControllerApi {

    @Autowired
    private AppShowBehaviorService appShowBehaviorService;
    @Autowired
    private AppLikesBehaviorService appLikesBehaviorService;
    @Autowired
    private AppUnLikesBehaviorService appUnLikesBehaviorService;
    @Autowired
    private AppReadBehaviorService appReadBehaviorService;

    @Override
    @PostMapping("/save_behavior")
    public ResponseResult saveShowBehavior(@RequestBody ShowBehaviorDto dto) {
        System.err.println(dto);
        return appShowBehaviorService.saveShowBehavior(dto);
    }

    @Override
    @PostMapping("/like_behavior")
    public ResponseResult saveLikesBehavior(@RequestBody LikesBehaviorDto dto) {
        return appLikesBehaviorService.saveLikesBehavior(dto);
    }

    @Override
    @PostMapping("/unlike_behavior")
    public ResponseResult saveUnLikesBehavior(@RequestBody UnLikesBehaviorDto dto) {
        return appUnLikesBehaviorService.saveUnLikesBehavior(dto);
    }

    @Override
    @PostMapping("/read_behavior")
    public ResponseResult saveReadBehavior(@RequestBody ReadBehaviorDto dto) {
        return appReadBehaviorService.saveReadBehavior(dto);
    }
}
