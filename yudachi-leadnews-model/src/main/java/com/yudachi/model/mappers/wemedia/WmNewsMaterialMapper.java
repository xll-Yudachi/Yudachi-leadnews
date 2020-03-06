package com.yudachi.model.mappers.wemedia;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface WmNewsMaterialMapper {

    /**
     * @Params [mid]
     * @Return int
     * @Description 查看指定的素材是否出现在素材与新闻关联表里面，没有则可以进行进一步的删除操作
     **/
    int countByMid(Integer mid);

    /**
     * @Params [nid]
     * @Return int
     * @Description 根据id删除新闻内容
     **/
    int delByNewsId(Integer nid);

    /**
     * @Author Yudachi
     * @Date 2020/2/20 21:00
     * @Version 1.0
     * @Description 保存文章和图片的关联关系
     **/
    void saveRelationsByContent(@Param("materials") Map<String, Object> materials, @Param("newsId") Integer newsId, @Param("type") Short type);

}
