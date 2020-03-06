package com.yudachi.media.service;

import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.media.dtos.WmMaterialDto;
import com.yudachi.model.media.dtos.WmMaterialListDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author Yudachi
 * @Date 2020/2/20 0:20
 * @Version 1.0
 * @Description 素材库服务
 **/
public interface MaterialService {
    /**
     * 上传图片接口*
     * @param multipartFile*
     * @return*
     */
    ResponseResult uploadPicture(MultipartFile multipartFile);

    /**
     * @Params [dto]
     * @Return com.yudachi.model.common.dtos.ResponseResult
     * @Description 删除图片
     **/
    ResponseResult delPicture(WmMaterialDto dto);

    /**
     * @Author Yudachi
     * @Date 2020/2/20 17:56
     * @Version 1.0
     * @Description 分页查询
     **/
    ResponseResult findList(WmMaterialListDto dto);

    /**
     * @Author Yudachi
     * @Date 2020/2/20 18:06
     * @Version 1.0
     * @Description 修改素材收藏状态
     **/
    ResponseResult changeUserMaterialStatus(WmMaterialDto dto, Short type);
    
}
