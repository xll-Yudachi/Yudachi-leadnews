package com.yudachi.model.mappers.app;

import com.yudachi.model.user.pojos.ApUser;

public interface ApUserMapper {

    ApUser selectById(Integer id);
    ApUser selectByApPhone(String phone);

}
