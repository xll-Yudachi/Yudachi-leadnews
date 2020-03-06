package com.yudachi.behavior;

import com.yudachi.behavior.service.AppLikesBehaviorService;
import com.yudachi.behavior.service.AppShowBehaviorService;
import com.yudachi.model.article.pojos.ApArticle;
import com.yudachi.model.behavior.dtos.LikesBehaviorDto;
import com.yudachi.model.behavior.dtos.ShowBehaviorDto;
import com.yudachi.model.user.pojos.ApUser;
import com.yudachi.utils.threadlocal.AppThreadLocalUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BehaviorTest {
    @Autowired
    private AppShowBehaviorService showBehaviorService;

    @Test
    public void testSaveBehavior() {
        ApUser apUser = new ApUser();
        apUser.setId(1l);
        AppThreadLocalUtils.setUser(apUser);
        ShowBehaviorDto dto = new ShowBehaviorDto();
        List<ApArticle> articles = new ArrayList<>();
        ApArticle apArticle = new ApArticle();
        apArticle.setId(233333);
        articles.add(apArticle);
        dto.setArticleIds(articles);
        showBehaviorService.saveShowBehavior(dto);
        //articleIndexService.saveBehaviors(data);
    }

    @Autowired
    private AppLikesBehaviorService appLikesBehaviorService;

    @Test
    public void testLikesSave(){
        ApUser user = new ApUser();
        user.setId(1l);
        AppThreadLocalUtils.setUser(user);
        LikesBehaviorDto dto = new LikesBehaviorDto();
        dto.setEntryId(10120);
        dto.setOperation((short)0);
        dto.setType((short)0);
        appLikesBehaviorService.saveLikesBehavior(dto);
    }

}
