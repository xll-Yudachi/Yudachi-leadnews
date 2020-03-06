package com.yudachi.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.yudachi.article.service.ApArticleSearchService;
import com.yudachi.article.utils.Trie;
import com.yudachi.common.common.contants.ESIndexConstants;
import com.yudachi.model.article.dtos.UserSearchDto;
import com.yudachi.model.article.pojos.ApArticle;
import com.yudachi.model.article.pojos.ApAssociateWords;
import com.yudachi.model.article.pojos.ApHotWords;
import com.yudachi.model.behavior.pojos.ApBehaviorEntry;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.common.enums.AppHttpCodeEnum;
import com.yudachi.model.mappers.app.*;
import com.yudachi.model.user.pojos.ApUser;
import com.yudachi.model.user.pojos.ApUserSearch;
import com.yudachi.utils.threadlocal.AppThreadLocalUtils;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("all")
public class ApArticleSearchServiceImpl implements ApArticleSearchService {

    @Autowired
    private ApAssociateWordsMapper apAssociateWordsMapper;
    @Autowired
    private ApHotWordsMapper apHotWordsMapper;
    @Autowired
    private ApBehaviorEntryMapper apBehaviorEntryMapper;
    @Autowired
    private ApUserSearchMapper apUserSearchMapper;
    @Autowired
    private ApArticleMapper apArticleMapper;
    @Autowired
    private JestClient jestClient;

    // 用户搜索历史
    @Override
    public ResponseResult findUserSearch(UserSearchDto dto) {
        if (dto.getPageSize() > 50) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ResponseResult ret = getEntryId(dto);
        if (ret.getCode() != AppHttpCodeEnum.SUCCESS.getCode()) {
            return ret;
        }
        List<ApUserSearch> list = apUserSearchMapper.selectByEntryId((Integer) ret.getData(), dto.getPageSize());
        return ResponseResult.okResult(list);
    }

    // 删除搜索历史
    @Override
    public ResponseResult delUserSearch(UserSearchDto dto) {
        if(dto.getHisList() ==null || dto.getHisList().size()<=0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        ResponseResult ret = getEntryId(dto);
        if(ret.getCode()!=AppHttpCodeEnum.SUCCESS.getCode()){
            return ret;
        }
        List<Integer> ids = dto.getHisList().stream().map(ApUserSearch::getId).collect(Collectors.toList());
        int rows = apUserSearchMapper.delUserSearch((Integer) ret.getData(),ids);
        return ResponseResult.okResult(rows);
    }

    // 清空搜索历史
    @Override
    public ResponseResult clearUserSearch(UserSearchDto dto) {
        ResponseResult ret = getEntryId(dto);
        if(ret.getCode()!=AppHttpCodeEnum.SUCCESS.getCode()){
            return ret;
        }
        int rows = apUserSearchMapper.clearUserSearch((Integer) ret.getData());
        return ResponseResult.okResult(rows);
    }

    // 热词搜索
    @Override
    public ResponseResult hotKeywords(String date) {
        if(StringUtils.isEmpty(date)){
            date = DateFormatUtils.format(new Date(), "yyyy-MM-dd");
        }
        List<ApHotWords> list = apHotWordsMapper.queryByHotDate(date);
        return ResponseResult.okResult(list);
    }

    // 联想词查询
    @Override
    public ResponseResult searchAssociate(UserSearchDto dto) {
        if(dto.getPageSize()>50 || dto.getPageSize() < 1){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        List<ApAssociateWords> aw = apAssociateWordsMapper.selectByAssociateWords("%"+dto.getSearchWords()+"%", dto.getPageSize());
        return ResponseResult.okResult(aw);
    }

    // 保存搜索记录
    @Override
    public ResponseResult saveUserSearch(Integer entryId, String searchWords) {
        //查询生效的记录是否存在
        int count = apUserSearchMapper.checkExist(entryId, searchWords);
        if(count>0){
            return ResponseResult.okResult(1);
        }
        ApUserSearch apUserSearch = new ApUserSearch();
        apUserSearch.setEntryId(entryId);
        apUserSearch.setKeyword(searchWords);
        apUserSearch.setStatus(1);
        apUserSearch.setCreatedTime(new Date());
        int row = apUserSearchMapper.insert(apUserSearch);
        return ResponseResult.okResult(row);
    }

    // ES文章分页搜索
    @Override
    public ResponseResult esArticleSearch(UserSearchDto dto) {
        //搜索词的敏感检查
        //只在第一页进行保存操作
        if(dto.getFromIndex()==0){
            ResponseResult result = getEntryId(dto);
            if(result.getCode()!=AppHttpCodeEnum.SUCCESS.getCode()){
                return result;
            }
            this.saveUserSearch((int)result.getData(),dto.getSearchWords());
        }
        //根据关键字查询索引库
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("title",dto.getSearchWords()));
        //设置分页
        searchSourceBuilder.from(dto.getFromIndex());
        searchSourceBuilder.size(dto.getPageSize());
        Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(ESIndexConstants.ARTICLE_INDEX).addType(ESIndexConstants.DEFAULT_DOC).build();
        try {
            SearchResult searchResult = jestClient.execute(search);
            List<ApArticle> sourceAsObjectList = searchResult.getSourceAsObjectList(ApArticle.class);
            List<ApArticle> resultList = new ArrayList<>();
            // 同步数据库
            for (ApArticle apArticle : sourceAsObjectList) {
                apArticle = apArticleMapper.selectById(Long.valueOf(apArticle.getId()));
                if(apArticle==null){
                    continue;
                }
                resultList.add(apArticle);
            }
            return ResponseResult.okResult(resultList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
    }

    public ResponseResult getEntryId(UserSearchDto dto) {
        ApUser user = AppThreadLocalUtils.getUser();
        // 用户和设备不能同时为空
        if (user == null && dto.getEquipmentId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        Long userId = null;
        if (user != null) {
            userId = user.getId();
        }
        ApBehaviorEntry apBehaviorEntry = apBehaviorEntryMapper.selectByUserIdOrEquipment(userId == null ? null : userId.intValue(), dto.getEquipmentId());
        // 行为实体找以及注册了，逻辑上这里是必定有值得，除非参数错误
        if (apBehaviorEntry == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        return ResponseResult.okResult(apBehaviorEntry.getId());
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ResponseResult searchAssociateV2(UserSearchDto dto) {
        if(dto.getPageSize()>50){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        String assoStr = redisTemplate.opsForValue().get("associate_list");
        List<ApAssociateWords> aw = null;
        if(StringUtils.isNotEmpty(assoStr)){
            aw = JSON.parseArray(assoStr, ApAssociateWords.class);
        }else{
            aw = apAssociateWordsMapper.selectAllAssociateWords();
            redisTemplate.opsForValue().set("associate_list", JSON.toJSONString(aw));
        }
        //needed cache trie
        Trie t = new Trie();
        for (ApAssociateWords a : aw){
            t.insert(a.getAssociateWords());
        }
        List<String> ret = t.startWith(dto.getSearchWords());
        List<ApAssociateWords> wrapperList = Lists.newArrayList();
        for(String s : ret){
            ApAssociateWords apAssociateWords = new ApAssociateWords();
            apAssociateWords.setAssociateWords(s);
            wrapperList.add(apAssociateWords);
        }
        return ResponseResult.okResult(wrapperList);
    }
}
