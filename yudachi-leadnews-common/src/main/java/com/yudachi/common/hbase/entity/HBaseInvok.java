package com.yudachi.common.hbase.entity;

/**
 * Hbase 的回调类
 * 用于我们操作的时候就行回调
 */
public interface HBaseInvok {
    /**
     * 回调方法
     */
    public void invok();
}