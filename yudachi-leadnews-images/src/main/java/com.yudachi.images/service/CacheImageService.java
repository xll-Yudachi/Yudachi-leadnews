package com.yudachi.images.service;

public interface CacheImageService {
    /**
     * 缓存图片到redis
     * @param imgUrl
     */
    byte[] cache2Redis(String imgUrl, boolean isCache);

    /**
     * 延长图片缓存
     * @param imageKey
     */
    void resetCache2Redis(String imageKey);
}
