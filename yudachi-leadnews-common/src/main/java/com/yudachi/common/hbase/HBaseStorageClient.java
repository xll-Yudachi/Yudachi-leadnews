package com.yudachi.common.hbase;

import com.yudachi.common.common.storage.StorageData;
import com.yudachi.common.hbase.entity.HBaseStorage;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HBase存储客户端
 */
@Component
@Log4j2
@SuppressWarnings("all")
@Data
public class HBaseStorageClient {

    /**
     * 注入的HBaseClent 工具类
     */
    @Autowired
    private HBaseClient hBaseClient;

    /**
     * 添加一个存储列表到Hbase
     *
     * @param tableName        表明
     * @param hBaseStorageList 存储列表
     */
    public void addHBaseStorage(final String tableName, List<HBaseStorage> hBaseStorageList) {
        if (null != hBaseStorageList && !hBaseStorageList.isEmpty()) {
            hBaseStorageList.stream().forEach(hBaseStorage -> {
                addHBaseStorage(tableName, hBaseStorage);
            });
        }
    }

    /**
     * 添加一个存储到Hbase
     *
     * @param tableName    表明
     * @param hBaseStorage 存储
     */
    public void addHBaseStorage(String tableName, HBaseStorage hBaseStorage) {
        if (null != hBaseStorage && StringUtils.isNotEmpty(tableName)) {
            hBaseClient.creatTable(tableName, hBaseStorage.getColumnFamily());
            String rowKey = hBaseStorage.getRowKey();
            List<StorageData> storageDataList = hBaseStorage.getDataList();
            boolean result = addStorageData(tableName, rowKey, storageDataList);
            if (result) {
                hBaseStorage.invok();
            }
        }

    }

    /**
     * 添加 数据到Hbase
     *
     * @param tableName       表明
     * @param rowKey          主键
     * @param storageDataList 存储数据集合
     * @return
     */
    public boolean addStorageData(String tableName, String rowKey, List<StorageData> storageDataList) {
        long currentTime = System.currentTimeMillis();
        log.info("开始添加StorageData到Hbase,tableName:{},rowKey:{}", tableName, rowKey);
        if (null != storageDataList && !storageDataList.isEmpty()) {
            storageDataList.forEach(hBaseData -> {
                String columnFamliyName = hBaseData.getTargetClassName();
                String[] columnArray = hBaseData.getColumns();
                String[] valueArray = hBaseData.getValues();
                if (null != columnArray && null != valueArray) {
                    hBaseClient.putData(tableName, rowKey, columnFamliyName, columnArray, valueArray);
                }
            });
        }
        log.info("添加StorageData到Hbase完成,tableName:{},rowKey:{},duration:{}", tableName, rowKey, System.currentTimeMillis() - currentTime);
        return true;
    }

    /**
     * 根据表明以及rowKey 获取一个对象
     *
     * @param tableName 表明
     * @param rowKey    主键
     * @param tClass    需要获取的对象类型
     * @param <T>       泛型T
     * @return 返回要返回的数
     */
    public <T> T getStorageDataEntity(String tableName, String rowKey, Class<T> tClass) {
        T tValue = null;
        if (StringUtils.isNotEmpty(tableName)) {
            StorageData hBaseData = hBaseClient.getStorageData(tableName, rowKey, tClass.getName());
            if (null != hBaseData) {
                tValue = (T) hBaseData.getObjectValue();
            }
        }
        return tValue;
    }

    /**
     * 根据 类型列表 ，表明 rowkey 返回一个数据类型的列表
     *
     * @param tableName 表明
     * @param rowKey    rowKey
     * @param typeList  类型列表
     * @return 返回的对象列表
     */
    public List<Object> getStorageDataEntityList(String tableName, String rowKey, List<Class> typeList) {
        List<Object> entityList = new ArrayList<Object>();
        List<String> strTypeList = typeList.stream().map(x -> x.getName()).collect(Collectors.toList());
        List<StorageData> storageDataList = hBaseClient.getStorageDataList(tableName, rowKey, strTypeList);
        for (StorageData storageData : storageDataList) {
            entityList.add(storageData.getObjectValue());
        }
        return entityList;
    }

    /**
     * 获取HBaseClent 客户端
     *
     * @return
     */
    public HBaseClient gethBaseClent() {
        return hBaseClient;
    }
}
