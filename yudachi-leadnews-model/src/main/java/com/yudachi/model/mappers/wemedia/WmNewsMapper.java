package com.yudachi.model.mappers.wemedia;

import com.yudachi.model.media.dtos.WmNewsPageReqDto;
import com.yudachi.model.media.pojos.WmNews;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WmNewsMapper {

    /**
     * 根据主键修改
     * @param dto
     * @return
     */
    int updateByPrimaryKey(WmNews dto);
    /**
     * 添加草稿新闻
     * @param dto
     * @return
     */
    int insertNewsForEdit(WmNews dto);

    /**
     * 查询根据dto条件
     * @param dto
     * @param uid
     * @return
     */
    List<WmNews> selectBySelective(@Param("dto") WmNewsPageReqDto dto, @Param("uid") Long uid);

    /**
     * 查询总数统计
     * @param dto
     * @param uid
     * @return
     */
    int countSelectBySelective(@Param("dto") WmNewsPageReqDto dto,@Param("uid") Long uid);

    /**
     * @Params [id]
     * @Return com.yudachi.model.media.pojos.WmNews
     * @Description 文章详情
     **/
    WmNews selectNewsDetailByPrimaryKey(Integer id);

    WmNews selectByPrimaryKey(Integer id);

    int deleteByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WmNews record);
}
