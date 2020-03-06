package com.yudachi.media.controller.v1;

import com.yudachi.common.common.contants.WmMediaConstans;
import com.yudachi.media.apis.MaterialManageControllerApi;
import com.yudachi.media.service.MaterialService;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.media.dtos.WmMaterialDto;
import com.yudachi.model.media.dtos.WmMaterialListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/media/material")
public class MaterialManageController implements MaterialManageControllerApi{

    @Autowired
    private MaterialService materialService;

    @PostMapping("/upload_picture")
    @Override
    public ResponseResult uploadPicture(MultipartFile file) {
        return materialService.uploadPicture(file);
    }

    @PostMapping("/del_picture")
    @Override
    public ResponseResult delPicture(@RequestBody WmMaterialDto dto) {
        return materialService.delPicture(dto);
    }

    @RequestMapping("/list")
    @Override
    public ResponseResult list(@RequestBody WmMaterialListDto dto) {
        return materialService.findList(dto);
    }

    @PostMapping("/collect")
    @Override
    public ResponseResult collectionMaterial(@RequestBody WmMaterialDto dto) {
        return materialService.changeUserMaterialStatus(dto,WmMediaConstans.COLLECT_MATERIAL);
    }

    @PostMapping("/cancel_collect")
    @Override
    public ResponseResult cancleCollectionMaterial(@RequestBody WmMaterialDto dto) {
        return materialService.changeUserMaterialStatus(dto,WmMediaConstans.CANCEL_COLLECT_MATERIAL);
    }
}
