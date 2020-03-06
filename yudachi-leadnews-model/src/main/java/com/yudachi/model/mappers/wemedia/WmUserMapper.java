package com.yudachi.model.mappers.wemedia;

import com.yudachi.model.media.pojos.WmUser;

public interface WmUserMapper {
    WmUser selectByName(String name);

    WmUser selectById(Integer id);
}
