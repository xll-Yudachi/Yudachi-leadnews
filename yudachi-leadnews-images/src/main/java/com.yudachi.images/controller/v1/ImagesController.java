package com.yudachi.images.controller.v1;

import com.yudachi.images.service.CacheImageService;
import com.yudachi.utils.common.Base64Utils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

@RestController
@RequestMapping(value = "api/v1/images")
@Log4j2
public class ImagesController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CacheImageService cacheImageService;
    @Value("${PREFIX}")
    private String PREFIX;

    @GetMapping(value = "get", produces = MediaType.IMAGE_JPEG_VALUE)
    public BufferedImage getImage(String u) throws Exception {
        String path = u;
        if(!u.startsWith("http")){
            path = PREFIX + u;
        }
        log.info("图片访问请求开始#path:{}", path);
        String baseCode = redisTemplate.opsForValue().get(path);
        //不存在从FastDFS中读取
        if(StringUtils.isEmpty(baseCode)){
            byte[] cache = cacheImageService.cache2Redis(path, false);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(cache));
            return bufferedImage;
        }
        byte[] source = Base64Utils.decode(baseCode);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(source));
        log.info("图片访问请求结束#path:{}", path);
        return bufferedImage;
    }
}
