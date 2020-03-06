package com.yudachi.images.apis;


import java.awt.image.BufferedImage;

public interface ImagesControllerApi {
    /**
     * 访问缓存图片
     * @param imagePath
     * @return
     * @throws Exception
     */
    public BufferedImage getImage(String imagePath) throws Exception;
}
