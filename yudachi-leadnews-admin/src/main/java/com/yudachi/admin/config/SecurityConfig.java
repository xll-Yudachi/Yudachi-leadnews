package com.yudachi.admin.config;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ServletComponentScan("com.yudachi.common.web.admin.security")
public class SecurityConfig {
}
