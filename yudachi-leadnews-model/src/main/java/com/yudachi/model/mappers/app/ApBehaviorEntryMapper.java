package com.yudachi.model.mappers.app;

import com.yudachi.model.behavior.pojos.ApBehaviorEntry;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApBehaviorEntryMapper {
    ApBehaviorEntry selectByUserIdOrEquipment(@Param("userId") Integer userId, @Param("equipmentId") Integer equipmentId);

    List<ApBehaviorEntry> selectAllEntry();
}
