package com.yudachi.admin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.yudachi.common.mysql.core")
@MapperScan("com.yudachi.admin.dao")
public class MysqlConfig {
}
