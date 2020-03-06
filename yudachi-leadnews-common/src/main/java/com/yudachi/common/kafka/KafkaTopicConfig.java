package com.yudachi.common.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@ConfigurationProperties(prefix="kafka.topic")
@PropertySource("classpath:kafka.properties")
public class KafkaTopicConfig {
    String submitArticleAuth;

    // 更新文章数据的消息topic
    String articleUpdateBus;

    //文章增量流处理完毕 处理结果监听主题
    String articleIncrHandle;

    String hotArticle;

    /**
     * 审核成功
     */
    String articleAuditSuccess;
}