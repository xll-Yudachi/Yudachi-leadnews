package com.yudachi.article;

import com.yudachi.article.service.AppArticleService;
import com.yudachi.common.common.article.constans.ArticleConstans;
import com.yudachi.model.article.dtos.ArticleHomeDto;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.user.pojos.ApUser;
import com.yudachi.utils.threadlocal.AppThreadLocalUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 测试文章列表相关接口
 */
@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
public class ArticleTest {
    @Autowired
    private AppArticleService appArticleService;

    /**
     * 测试load
     */
    @Test
    public void testLoad() {
        ApUser apUser = new ApUser();
        apUser.setId(2104l);
        AppThreadLocalUtils.setUser(apUser);
        ArticleHomeDto dto = new ArticleHomeDto();
        ResponseResult data = appArticleService.load( ArticleConstans.LOADTYPE_LOAD_MORE, dto);
        System.err.println(data.getData());
    }
}
