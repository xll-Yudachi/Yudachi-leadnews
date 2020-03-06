package com.yudachi.admin.service;

import com.yudachi.model.admin.dtos.CommonDto;
import com.yudachi.model.common.dtos.ResponseResult;

public interface CommonService {

    /**
     * 加载通用的数据列表
     * @param dto
     * @return
     */
    ResponseResult list(CommonDto dto);

    /**
     * 修改通用的数据列表
     * @param dto
     * @return
     */
    ResponseResult update(CommonDto dto);

    /**
     * 删除通用的数据列表
     * @param dto
     * @return
     */
    ResponseResult delete(CommonDto dto);

}
