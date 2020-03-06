package com.yudachi.migration;

import com.yudachi.common.hbase.HBaseClient;
import org.apache.hadoop.hbase.client.Result;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@SuppressWarnings("all")
public class HbaseTest {

    @Autowired
    private HBaseClient hBaseClient;

    @Test
    public void testCreateTable(){

        List<String> columnFamily = new ArrayList<>();
        columnFamily.add("test_cloumn_family1");
        columnFamily.add("test_cloumn_family2");
        boolean ret = hBaseClient.creatTable("hbase_test_table_name", columnFamily);
    }

    @Test
    public void testDelTable(){
        hBaseClient.deleteTable("hbase_test_table_name");
    }

    @Test
    public void testSaveData(){
        String []columns ={"name","age"};
        String [] values = {"zhangsan","28"};
        hBaseClient.putData("hbase_test_table_name","test_row_key_001","test_cloumn_family1",columns,values);
    }

    @Test
    public void testFindByRowKey(){
        Result hbaseResult = hBaseClient.getHbaseResult("hbase_test_table_name", "test_row_key_001");
        System.out.println(hbaseResult);
    }
}