package com.yudachi.model.behavior.dtos;

import com.yudachi.model.annotation.IdEncrypt;
import lombok.Data;

@Data
public class FollowBehaviorDto {
    // 设备ID
    @IdEncrypt
    Integer equipmentId;
    // 文章ID
    @IdEncrypt
    Integer articleId;
    @IdEncrypt
    Integer followId;
}
