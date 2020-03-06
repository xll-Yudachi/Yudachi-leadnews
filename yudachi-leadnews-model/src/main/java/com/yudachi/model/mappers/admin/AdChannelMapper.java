package com.yudachi.model.mappers.admin;

import com.yudachi.model.admin.pojos.AdChannel;

import java.util.List;

public interface AdChannelMapper {
    /**
     * 查询所有
     */
    public List<AdChannel> selectAll();

    /**
     * 根据id查询频道
     * @param id
     * @return: com.yudachi.model.admin.pojos.AdChannel
     **/
    AdChannel selectByPrimaryKey(Integer id);
}
