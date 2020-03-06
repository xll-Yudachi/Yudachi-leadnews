package com.yudachi.migration;

import com.yudachi.common.common.contants.HBaseConstants;
import com.yudachi.common.common.storage.StorageData;
import com.yudachi.common.hbase.HBaseStorageClient;
import com.yudachi.common.mongo.entity.MongoStorageEntity;
import com.yudachi.model.article.pojos.ApArticle;
import com.yudachi.model.article.pojos.ApArticleContent;
import com.yudachi.model.article.pojos.ApAuthor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = MigrationApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings("all")
public class MongoTest {

    @Autowired
    private MongoTemplate mongotemplate;


    @Autowired
    private HBaseStorageClient hBaseStorageClient;


    @Test
    public void test() {
        Class<?>[] classes = new Class<?>[]{ApArticle.class, ApArticleContent.class, ApAuthor.class};
        //List<Object> entityList = hBaseStorageClient.getHbaseDataEntityList(HBaseConstants.APARTICLE_QUANTITY_TABLE_NAME, "1", Arrays.asList(classes));
        List<String> strList = Arrays.asList(classes).stream().map(x -> x.getName()).collect(Collectors.toList());
        List<StorageData> storageDataList = hBaseStorageClient.gethBaseClent().getStorageDataList(HBaseConstants.APARTICLE_QUANTITY_TABLE_NAME, "1", strList);
        MongoStorageEntity mongoStorageEntity = new MongoStorageEntity();
        mongoStorageEntity.setDataList(storageDataList);
        mongoStorageEntity.setRowKey("1");
        MongoStorageEntity tmp = mongotemplate.findById("1", MongoStorageEntity.class);
        if (null != tmp) {
            mongotemplate.remove(tmp);
        }
        MongoStorageEntity tq = mongotemplate.insert(mongoStorageEntity);
        System.out.println(tq);

    }

    @Test
    public void test1() {
        Criteria criteria = new Criteria();
        criteria.where("rowKey").equals("1");
        Query query = new Query(criteria);
        List<MongoStorageEntity> mongoStorageEntities = mongotemplate.find(query, MongoStorageEntity.class);
        MongoStorageEntity mongoStorageEntity = mongoStorageEntities.get(0);
        System.err.println(mongoStorageEntity);
        if (null != mongoStorageEntity && null != mongoStorageEntity.getDataList()) {
            mongoStorageEntity.getDataList().forEach(x -> {
                System.out.println(x.getObjectValue());
            });
        }
    }
}
