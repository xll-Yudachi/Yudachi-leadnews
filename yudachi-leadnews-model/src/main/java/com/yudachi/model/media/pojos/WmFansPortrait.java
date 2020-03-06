package com.yudachi.model.media.pojos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yudachi.model.annotation.IdEncrypt;
import lombok.Data;

import java.util.Date;

@Data
public class WmFansPortrait {
    private Integer id;
    @IdEncrypt
    private Integer userId;
    private String name;
    private String value;
    @JsonIgnore
    private String burst;
    private Date createdTime;
}