package com.yudachi.media.controller.v1;

import com.yudachi.media.apis.AdChannelControllerApi;
import com.yudachi.media.service.AdChannelService;
import com.yudachi.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/channel")
public class AdChannelController implements AdChannelControllerApi {

    @Autowired
    private AdChannelService channelService ;

    @Override
    @RequestMapping("/channels")
    public ResponseResult selectAll(){
        return ResponseResult.okResult(channelService.selectAll());
    }

}
