package com.yudachi.media.service.impl;

import com.yudachi.media.service.AdChannelService;
import com.yudachi.model.admin.pojos.AdChannel;
import com.yudachi.model.mappers.admin.AdChannelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SuppressWarnings("all")
public class AdChannelServiceImpl implements AdChannelService {

    @Autowired
    private AdChannelMapper channelMapper;

    @Override
    public List<AdChannel> selectAll() {
        return channelMapper.selectAll();
    }
}
