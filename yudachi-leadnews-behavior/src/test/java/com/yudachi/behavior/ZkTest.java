package com.yudachi.behavior;

import com.yudachi.common.zookeeper.Sequences;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = BehaviorApplication.class)
@RunWith(SpringRunner.class)
public class ZkTest {

    @Autowired
    private Sequences sequences;

    @Test
    public void ZkTest(){
        for (int i = 0; i < 50; i++) {
            Long id = sequences.sequenceApCollection();
            System.out.println(id);
        }
    }
}
