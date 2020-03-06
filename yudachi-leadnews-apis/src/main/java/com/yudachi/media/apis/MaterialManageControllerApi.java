package com.yudachi.media.apis;

import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.media.dtos.WmMaterialDto;
import com.yudachi.model.media.dtos.WmMaterialListDto;
import org.springframework.web.multipart.MultipartFile;

public interface MaterialManageControllerApi {
    /**
     * 上传图片
     * @param multipartFile
     * @return
     */
    ResponseResult uploadPicture(MultipartFile multipartFile);

    /**
     * @Params [wmMaterial]
     * @Return com.yudachi.model.common.dtos.ResponseResult
     * @Description 删除图片
     **/
    ResponseResult delPicture(WmMaterialDto wmMaterial);

    /**
     * @Params [dto]
     * @Return com.yudachi.model.common.dtos.ResponseResult
     * @Description 分页查询
     **/
    ResponseResult list(WmMaterialListDto dto);

    /**
     * @Params [dto]
     * @Return com.yudachi.model.common.dtos.ResponseResult
     * @Description 收藏
     **/
    ResponseResult collectionMaterial(WmMaterialDto dto);

    /**
     * @Params [dto]
     * @Return com.yudachi.model.common.dtos.ResponseResult
     * @Description 取消收藏
     **/
    ResponseResult cancleCollectionMaterial(WmMaterialDto dto);
}
