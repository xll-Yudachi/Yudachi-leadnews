package com.yudachi.model.admin.pojos;

import lombok.Data;

import java.util.Date;

@Data
public class AdMenu {
    private Integer id;
    private String name;
    private String code;
    private Integer parentId;
    private Date createdTime;

}