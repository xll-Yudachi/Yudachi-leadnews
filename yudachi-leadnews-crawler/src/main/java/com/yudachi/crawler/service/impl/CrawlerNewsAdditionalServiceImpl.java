package com.yudachi.crawler.service.impl;

import com.yudachi.crawler.service.CrawlerNewsAdditionalService;
import com.yudachi.model.crawler.core.parse.ParseItem;
import com.yudachi.model.crawler.core.parse.impl.CrawlerParseItem;
import com.yudachi.model.crawler.pojos.ClNewsAdditional;
import com.yudachi.model.mappers.crawerls.ClNewsAdditionalMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@SuppressWarnings("all")
public class CrawlerNewsAdditionalServiceImpl implements CrawlerNewsAdditionalService {

    @Autowired
    private ClNewsAdditionalMapper clNewsAdditionalMapper;

    public List<ClNewsAdditional> queryList(ClNewsAdditional clNewsAdditional) {
        return clNewsAdditionalMapper.selectList(clNewsAdditional);
    }

    @Override
    public ClNewsAdditional getAdditionalByUrl(String url) {
        ClNewsAdditional clNewsAdditional = new ClNewsAdditional();
        clNewsAdditional.setUrl(url);
        List<ClNewsAdditional> additionalList = queryList(clNewsAdditional);
        if (null != additionalList && !additionalList.isEmpty()) {
            return additionalList.get(0);
        }
        return null;
    }


    /**
     * 是否是已存在的URL
     *
     * @return
     */
    public boolean isExistsUrl(String url) {
        boolean isExistsUrl = false;
        if (StringUtils.isNotEmpty(url)) {
            ClNewsAdditional clNewsAdditional = getAdditionalByUrl(url);
            if (null != clNewsAdditional) {
                isExistsUrl = true;
            }
        }
        return isExistsUrl;
    }

    @Override
    public boolean checkExist(String url) {
        ClNewsAdditional clNewsAdditional = new ClNewsAdditional();
        clNewsAdditional.setUrl(url);
        List<ClNewsAdditional> clNewsAdditionalList = clNewsAdditionalMapper.selectList(clNewsAdditional);
        if (null != clNewsAdditionalList && !clNewsAdditionalList.isEmpty()) {
            return true;
        }
        return false;
    }


    @Override
    public void updateAdditional(ClNewsAdditional clNewsAdditional) {
        clNewsAdditionalMapper.updateByPrimaryKeySelective(clNewsAdditional);
    }

    @Override
    public void saveAdditional(ClNewsAdditional clNewsAdditional) {
        clNewsAdditionalMapper.insertSelective(clNewsAdditional);
    }

    @Override
    public List<ClNewsAdditional> queryListByNeedUpdate(Date currentDate) {
        return clNewsAdditionalMapper.selectListByNeedUpdate(currentDate);
    }

    /**
     * 转换为ParseItem
     *
     * @param additionalList
     * @return
     */
    public List<ParseItem> toParseItem(List<ClNewsAdditional> additionalList) {
        List<ParseItem> parseItemList = new ArrayList<ParseItem>();
        if (null != additionalList && !additionalList.isEmpty()) {
            for (ClNewsAdditional additional : additionalList) {
                ParseItem parseItem = toParseItem(additional);
                if (null != parseItem) {
                    parseItemList.add(parseItem);
                }
            }
        }
        return parseItemList;
    }

    private ParseItem toParseItem(ClNewsAdditional additional) {
        CrawlerParseItem crawlerParseItem = null;
        if (null != additional) {
            crawlerParseItem = new CrawlerParseItem();
            crawlerParseItem.setUrl(additional.getUrl());
        }
        return crawlerParseItem;
    }

    /**
     * 获取增量统计数据
     * @return
     */
    public List<ParseItem> queryIncrementParseItem(Date currentDate) {
        List<ClNewsAdditional> clNewsAdditionalList = queryListByNeedUpdate(currentDate);
        List<ParseItem> parseItemList = toParseItem(clNewsAdditionalList);
        return parseItemList;
    }
}