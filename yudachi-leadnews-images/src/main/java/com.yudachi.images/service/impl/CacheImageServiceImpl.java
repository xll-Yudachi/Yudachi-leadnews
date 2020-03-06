package com.yudachi.images.service.impl;

import com.google.common.collect.Maps;
import com.yudachi.common.fastdfs.FastDfsClient;
import com.yudachi.images.service.CacheImageService;
import com.yudachi.utils.common.Base64Utils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class CacheImageServiceImpl implements CacheImageService {

    @Autowired
    private FastDfsClient fastDfsClient;
    @Autowired
    private StringRedisTemplate redisTemplate;

    final long EXPIRE = 60 * 60 * 24l; //24小时

    @Override
    public byte[] cache2Redis(String imgUrl, boolean isCache) {
        byte[] ret = null;
        log.info("缓存图片到redis#imgUrl:{},isCache:{}", imgUrl, isCache);
        Map<String,String> map = formatPath(imgUrl);
        String group = map.get("group");
        String file = map.get("file");
        String baseString = "";
        try {
            byte[] fileByte = fastDfsClient.downGroupFile(group, file);
            ret = fileByte;
            baseString = Base64Utils.encode(fileByte);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isCache){
            redisTemplate.opsForValue().set(imgUrl, baseString, EXPIRE, TimeUnit.SECONDS);
        }
        return ret;
    }

    @Override
    public void resetCache2Redis(String imageKey) {
        redisTemplate.expire(imageKey, EXPIRE, TimeUnit.SECONDS);
    }

    /**
     * 解析图片URL
     * @param imgUrl
     * @return
     */
    private Map<String,String> formatPath(String imgUrl){
        // http://192.168.202.200/group1/M00/00/00/wKjKyF5OgZyAUkYcAAUh-jNlOEk058.jpg
        Map<String,String> map = Maps.newHashMap();
        String groupString = imgUrl.substring(imgUrl.indexOf("group"),imgUrl.length());
        int index = groupString.indexOf("/");
        map.put("group", groupString.substring(0,index));
        map.put("file", groupString.substring(index+1,groupString.length()));
        return map;
    }
}