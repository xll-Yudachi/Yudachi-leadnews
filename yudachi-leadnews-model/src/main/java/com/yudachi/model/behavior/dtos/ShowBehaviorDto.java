package com.yudachi.model.behavior.dtos;


import com.yudachi.model.annotation.IdEncrypt;
import com.yudachi.model.article.pojos.ApArticle;
import lombok.Data;

import java.util.List;

@Data
public class ShowBehaviorDto {

    // 设备ID
    @IdEncrypt
    Integer equipmentId;
    List<ApArticle> articleIds;

}
