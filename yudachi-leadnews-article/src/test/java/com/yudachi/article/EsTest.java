package com.yudachi.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yudachi.common.common.contants.ESIndexConstants;
import com.yudachi.common.common.pojo.EsIndexEntity;
import com.yudachi.model.article.dtos.ArticleHomeDto;
import com.yudachi.model.article.dtos.UserSearchDto;
import com.yudachi.model.article.pojos.ApArticle;
import com.yudachi.model.article.pojos.ApArticleContent;
import com.yudachi.model.crawler.core.parse.ZipUtils;
import com.yudachi.model.mappers.app.ApArticleContentMapper;
import com.yudachi.model.mappers.app.ApArticleMapper;
import com.yudachi.model.user.pojos.ApUser;
import com.yudachi.utils.threadlocal.AppThreadLocalUtils;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SuppressWarnings("all")
public class EsTest {

    @Autowired
    private JestClient jestClient;

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @Test
    public void testSave() throws IOException {

        ArticleHomeDto dto = new ArticleHomeDto();
        dto.setSize(50);
        dto.setTag("__all__");
        List<ApArticle> apArticles = apArticleMapper.loadArticleListByLocation(dto, null);
        for (ApArticle apArticle : apArticles) {
            ApArticleContent apArticleContent = apArticleContentMapper.selectByArticleId(apArticle.getId());
            if (apArticleContent!=null){
                EsIndexEntity esIndexEntity = new EsIndexEntity();
                esIndexEntity.setChannelId(new Long(apArticle.getChannelId()));
                esIndexEntity.setId(apArticle.getId().longValue());
                esIndexEntity.setContent(ZipUtils.gunzip(apArticleContent.getContent()));
                esIndexEntity.setPublishTime(apArticle.getPublishTime());
                esIndexEntity.setStatus(new Long(1));
                esIndexEntity.setTag("article");
                esIndexEntity.setTitle(apArticle.getTitle());
                Index.Builder builder = new Index.Builder(esIndexEntity);
                builder.id(apArticle.getId().toString());
                builder.refresh(true);
                Index index = builder.index(ESIndexConstants.ARTICLE_INDEX).type(ESIndexConstants.DEFAULT_DOC).build();
                JestResult result = jestClient.execute(index);
                if (result != null && !result.isSucceeded()) {
                    throw new RuntimeException(result.getErrorMessage() + "插入更新索引失败!");
                }
            }
        }
    }

    @Test
    public void testEsArticleSearch() throws Exception {
        ApUser user = new ApUser();
        user.setId(4l);
        AppThreadLocalUtils.setUser(user);
        UserSearchDto dto = new UserSearchDto();
        dto.setEquipmentId(1);
        dto.setSearchWords("区块链");
        dto.setPageSize(20);
        dto.setPageNum(1);
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/api/v1/article/search/article_search")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsBytes(dto));
        mvc.perform(builder).andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print());
    }
}