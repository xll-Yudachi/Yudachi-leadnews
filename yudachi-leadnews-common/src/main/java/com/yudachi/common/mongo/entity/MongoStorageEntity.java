package com.yudachi.common.mongo.entity;

import com.yudachi.common.common.storage.StorageEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * mongoDB 存储实体
 */
@Document(collection = "mongo_storage_data")
@Data
public class MongoStorageEntity extends StorageEntity {
    /**
     * 主键的Key
     *
     * @Id 标明该字段是主键
     */
    @Id
    private String rowKey;

}
