package com.yudachi.article.config;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ServletComponentScan("com.yudachi.common.web.app.security")
public class SecurityConfig {
}
