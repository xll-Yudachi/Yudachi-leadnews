package com.yudachi.common.zookeeper;

import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.Map;

@Data
public class ZookeeperClient {

    private static Logger logger = LoggerFactory.getLogger(ZookeeperClient.class);
    private String host;
    private String sequencePath;

    // 重试休眠时间
    private final int SLEEP_TIME_MS = 1000;
    // 最大重试1000次
    private final int MAX_RETRIES = 1000;
    //会话超时时间
    private final int SESSION_TIMEOUT = 30 * 1000;
    //连接超时时间
    private final int CONNECTION_TIMEOUT = 3 * 1000;

    // 创建zk连接实例
    private CuratorFramework client = null;
    // 序列化集合，线程安全，存入后不允许修改
    private Map<String, ZkSequence> zkSequenceMap = Maps.newConcurrentMap();

    public ZookeeperClient(String host, String sequencePath){
        this.host = host;
        this.sequencePath = sequencePath;
    }

    @PostConstruct
    public void init(){
        // 初始化客户端参数
        this.client = CuratorFrameworkFactory.builder()
                .connectString(this.getHost())
                .connectionTimeoutMs(CONNECTION_TIMEOUT)
                .sessionTimeoutMs(SESSION_TIMEOUT)
                .retryPolicy(new ExponentialBackoffRetry(SLEEP_TIME_MS, MAX_RETRIES))
                .build();
        // 启动客户端
        this.client.start();
        // 初始化zk序列
        this.initZkSequence();

    }

    /**
     * @Description //初始化zk序列
     * @Params []
     * @Return void
     **/
    public void initZkSequence(){
        ZkSequenceEnum[] list = ZkSequenceEnum.values();
        for (int i = 0; i < list.length; i++) {
            String name = list[i].name();
            String path = this.sequencePath + name;
            ZkSequence zkSequence = new ZkSequence(path, client);
            zkSequenceMap.put(name, zkSequence);
        }
    }

    /**
     * @Description //生成zk序列
     * @Params [zkSequenceEnum] 序列名 枚举类
     * @Return java.lang.Long
     **/
    public Long sequence(ZkSequenceEnum zkSequenceEnum){
        try {
            ZkSequence zkSequence = zkSequenceMap.get(zkSequenceEnum.name());
            if (zkSequence != null){
                return zkSequence.sequence();
            }
        }catch (Exception e){
            logger.error("获取[{}]Sequence错误:{}",zkSequenceEnum,e);
        }
        return null;
    }


}
