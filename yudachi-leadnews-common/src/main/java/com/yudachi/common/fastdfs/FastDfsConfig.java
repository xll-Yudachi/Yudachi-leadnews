package com.yudachi.common.fastdfs;

import com.luhuiguo.fastdfs.FdfsAutoConfiguration;
import com.luhuiguo.fastdfs.FdfsProperties;
import com.luhuiguo.fastdfs.conn.FdfsConnectionPool;
import com.luhuiguo.fastdfs.conn.PooledConnectionFactory;
import com.luhuiguo.fastdfs.conn.TrackerConnectionManager;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;

@Data
@Configuration
@ConfigurationProperties(prefix = "fast.dfs")
@PropertySource("classpath:fast-dfs.properties")
@SuppressWarnings("all")
public class FastDfsConfig extends FdfsAutoConfiguration {

    int soTimeout;
    int connectTimeout;
    String trackerServer;

    public FastDfsConfig(FdfsProperties properties){
        super(properties);
    }

    @Bean
    public PooledConnectionFactory pooledConnectionFactory(){
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setSoTimeout(getSoTimeout());
        pooledConnectionFactory.setConnectTimeout(getConnectTimeout());
        return pooledConnectionFactory;
    }

    @Bean
    public TrackerConnectionManager trackerConnectionManager(FdfsConnectionPool fdfsConnectionPool){
        return new TrackerConnectionManager(fdfsConnectionPool, Arrays.asList(trackerServer));
    }
}
