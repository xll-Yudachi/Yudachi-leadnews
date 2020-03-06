package com.yudachi.article.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan("com.yudachi.common.quartz")
@EnableScheduling
public class QuartzConfig {
}
