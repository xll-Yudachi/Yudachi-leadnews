package com.yudachi.model.mappers.admin;

import com.yudachi.model.admin.pojos.AdUser;

public interface AdUserMapper {
    AdUser selectByName(String name);
}
