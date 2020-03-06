package com.yudachi.model.mappers.wemedia;

import com.yudachi.model.media.dtos.WmMaterialListDto;
import com.yudachi.model.media.pojos.WmMaterial;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface WmMaterialMapper {

    int insert(WmMaterial record);

    /**
     * @Params [id]
     * @Return com.yudachi.model.media.pojos.WmMaterial
     * @Description 依据id查询媒体文件
     **/
    WmMaterial selectByPrimaryKey(Integer id);

    /**
     * @Params [id]
     * @Return int
     * @Description 根据id删除图片
     **/
    int deleteByPrimaryKey(Integer id);

    List<WmMaterial> findListByUidAndStatus(@Param("dto") WmMaterialListDto dto, @Param("uid") Long uid);

    int countListByUidAndStatus(@Param("dto") WmMaterialListDto dto, @Param("uid") Long uid);

    /**
     * @Params [id, userId, type]
     * @Return int
     * @Description 按照文章ID和用户ID查询内容
     **/
    int updateStatusByUidAndId(@Param("id") Integer id, @Param("userId") Long userId, @Param("type") Short type);

    /**
     * @Params [uid, values]
     * @Return java.util.List<com.yudachi.model.media.pojos.WmMaterial>
     * @Description 按照素材Url和用户ID查询内容
     **/
    List<WmMaterial> findMaterialByUidAndimgUrls(@Param("uid") Long uid, @Param("values") Collection<Object> values);
}
