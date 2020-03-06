package com.yudachi.admin.controller.v1;

import com.yudachi.admin.service.CommonService;
import com.yudachi.model.admin.dtos.CommonDto;
import com.yudachi.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/common")
public class CommonController{

    @Autowired
    private CommonService commonService;

    @PostMapping("/list")
    public ResponseResult list(@RequestBody CommonDto dto) {
        return commonService.list(dto);
    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody CommonDto dto) {
        return commonService.update(dto);
    }

    @PostMapping("/delete")
    public ResponseResult delete(@RequestBody CommonDto dto) {
        return commonService.delete(dto);
    }

}