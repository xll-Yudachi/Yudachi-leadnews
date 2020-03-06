package com.yudachi.crawler.service.impl;
import com.yudachi.common.common.util.YudachiStringUtils;
import com.yudachi.crawler.service.AdLabelService;
import com.yudachi.model.admin.pojos.AdChannelLabel;
import com.yudachi.model.admin.pojos.AdLabel;
import com.yudachi.model.mappers.admin.AdChannelLabelMapper;
import com.yudachi.model.mappers.admin.AdLabelMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@SuppressWarnings("all")
public class AdLabelServiceImpl implements AdLabelService {

    @Autowired
    private AdLabelMapper adLabelMapper;

    @Autowired
    private AdChannelLabelMapper adChannelLabelMapper;

    @Override
    public String getLabelIds(String labels) {
        long currentTimeMillis = System.currentTimeMillis();
        log.info("获取channel信息，标签:labels:{}", labels);
        List<AdLabel> adLabelList = new ArrayList<>();
        if (StringUtils.isNotEmpty(labels)) {
            //转换成小写
            labels = labels.toLowerCase();
            List<String> tmpLabels = Arrays.asList(labels.split(","));
            tmpLabels = new ArrayList<>(tmpLabels);
            adLabelList = adLabelMapper.queryAdLabelByLabels(tmpLabels);
            if (null != adLabelList && !adLabelList.isEmpty()) {
                adLabelList = addLabelList(tmpLabels, adLabelList);
            } else {
                adLabelList = addLabelList(tmpLabels);
            }
        }
        List<String> labelList = adLabelList.stream().map(label -> YudachiStringUtils.toString(label.getId())).collect(Collectors.toList());
        String resultStr = YudachiStringUtils.listToStr(labelList, ",");
        log.info("获取channel信息完成，标签:{},labelIds:{},耗时:{}", labels, resultStr, System.currentTimeMillis() - currentTimeMillis);
        return resultStr;
    }

    /**
     * 过滤保存
     *
     * @param tmpLabels
     * @param adLabelList
     * @return
     */
    private List<AdLabel> addLabelList(List<String> tmpLabels, List<AdLabel> adLabelList) {
        if(tmpLabels!=null && !tmpLabels.isEmpty()){
            for(AdLabel adLabel : adLabelList){
                for (int i = 0;i<tmpLabels.size();i++) {
                    if(tmpLabels.get(i).contains(adLabel.getName())){
                        tmpLabels.remove(i);
                    }
                }
            }
        }
        if(tmpLabels!=null && !tmpLabels.isEmpty()){
            adLabelList.addAll(addLabelList(tmpLabels));
        }

        return adLabelList;

    }

    /**
     * 保存
     * @param tmpLabels
     * @return
     */
    private List<AdLabel> addLabelList(List<String> tmpLabels) {
        List<AdLabel> adLabelList = new ArrayList<>();
        for (String label : tmpLabels) {
            adLabelList.add(addLabel(label));
        }
        return adLabelList;
    }

    /**
     * 保存操作
     * @param label
     * @return
     */
    private AdLabel addLabel(String label) {
        AdLabel adLabel = new AdLabel();
        adLabel.setName(label);
        adLabel.setType(true);
        adLabel.setCreatedTime(new Date());
        adLabelMapper.insert(adLabel);
        return adLabel;
    }


    @Override
    public Integer getAdChannelByLabelIds(String labelIds) {
        Integer channelId = 0;
        try {
            channelId = getSecurityAdChannelByLabelIds(labelIds);
        }catch (Exception e){
            log.error("获取channel信息失败，errorMsg:{}",e.getMessage());
        }
        return channelId;
    }

    private Integer getSecurityAdChannelByLabelIds(String labelIds) {
        long currentTimeMillis = System.currentTimeMillis();
        log.info("获取channel信息，标签ids:{}",labelIds);
        Integer channelId = 0;
        if(StringUtils.isNotEmpty(labelIds)){//1,2,3
            List<String> labelList = Arrays.asList(labelIds.split(","));
            List<AdLabel> adLabelList = adLabelMapper.queryAdLabelByLabelIds(labelList);
            if(null != adLabelList && !adLabelList.isEmpty()){
                channelId = getAdChannelIdByLabelId(adLabelList.get(0).getId());
            }
            channelId = channelId == null ?0:channelId;
        }

        log.info("获取channel信息完成，标签:{},channelId:{},耗时:{}",labelIds,channelId,System.currentTimeMillis()-currentTimeMillis);
        return channelId;
    }

    private Integer getAdChannelIdByLabelId(Integer labelId) {
        Integer channelId = 0;
        AdChannelLabel adChannelLabel = adChannelLabelMapper.selectByLabelId(labelId);
        if(adChannelLabel!=null){
            channelId = adChannelLabel.getChannelId();
        }
        return channelId;
    }

    @Override
    public String getLableIds(String labels) {
        return null;
    }

}
