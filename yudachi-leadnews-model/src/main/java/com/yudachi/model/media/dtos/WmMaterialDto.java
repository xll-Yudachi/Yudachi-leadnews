package com.yudachi.model.media.dtos;

import com.yudachi.model.annotation.IdEncrypt;
import lombok.Data;

@Data
public class WmMaterialDto {

    @IdEncrypt
    private Integer id;

}
