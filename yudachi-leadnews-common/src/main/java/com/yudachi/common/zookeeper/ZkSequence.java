package com.yudachi.common.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZkSequence {

    // 500秒内重试三次，若失败则返回Null
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(500, 3);

    // zk分布式主键自增类
    DistributedAtomicLong distributedAtomicLong;

    public ZkSequence(String sequenceName, CuratorFramework client){
        distributedAtomicLong = new DistributedAtomicLong(client, sequenceName, retryPolicy);
    }

    public Long sequence() throws Exception {
        AtomicValue<Long> increment = this.distributedAtomicLong.increment();
        if (increment.succeeded()){
            // 返回自增后的值
            return increment.postValue();
        }else{
            return null;
        }
    }
}
