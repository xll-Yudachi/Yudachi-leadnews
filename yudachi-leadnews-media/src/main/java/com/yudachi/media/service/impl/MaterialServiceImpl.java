package com.yudachi.media.service.impl;

import com.yudachi.common.fastdfs.FastDfsClient;
import com.yudachi.media.service.MaterialService;
import com.yudachi.model.common.dtos.ResponseResult;
import com.yudachi.model.common.enums.AppHttpCodeEnum;
import com.yudachi.model.mappers.wemedia.WmMaterialMapper;
import com.yudachi.model.mappers.wemedia.WmNewsMaterialMapper;
import com.yudachi.model.media.dtos.WmMaterialDto;
import com.yudachi.model.media.dtos.WmMaterialListDto;
import com.yudachi.model.media.pojos.WmMaterial;
import com.yudachi.model.media.pojos.WmUser;
import com.yudachi.utils.threadlocal.WmThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@SuppressWarnings("all")
public class MaterialServiceImpl implements MaterialService {

    @Value("${FILE_SERVER_URL}")
    private String fileServerUrl;

    @Autowired
    private FastDfsClient fastDFSClient;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;
    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        WmUser user = WmThreadLocalUtils.getUser();
        if (multipartFile == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        String originFileName = multipartFile.getOriginalFilename();
        String extName = originFileName.substring(originFileName.lastIndexOf(".") + 1);
        if (!extName.matches("(gif|png|jpg|jpeg)")) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_IMAGE_FORMAT_ERROR);
        }

        String fileId = null;

        //上传图片获得文件id
        try {
            fileId = fastDFSClient.uploadFile(multipartFile.getBytes(), extName);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("user {} upload file {} to fastDFS error, error info:n", user.getId(), originFileName, e.getMessage());
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }

        //上传成功保存媒体资源到数据库
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setCreatedTime(new Date());
        wmMaterial.setType((short) 0);
        wmMaterial.setUrl(fileId);
        wmMaterial.setUserId(user.getId());
        wmMaterial.setIsCollection((short) 0);
        wmMaterialMapper.insert(wmMaterial);
        //设置返回值
        wmMaterial.setUrl(fileServerUrl + wmMaterial.getUrl());
        return ResponseResult.okResult(wmMaterial);
    }

    @Override
    public ResponseResult delPicture(WmMaterialDto dto) {
        WmUser user = WmThreadLocalUtils.getUser();
        if (dto == null || dto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //删除fastDFS上的文件
        WmMaterial wmMaterial = wmMaterialMapper.selectByPrimaryKey(dto.getId());
        if (wmMaterial == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        int count = wmNewsMaterialMapper.countByMid(dto.getId());

        if (count > 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "当前图片被引用");
        }

        String fileId = wmMaterial.getUrl().replace(fileServerUrl, "");

        try {
            fastDFSClient.delFile(fileId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("user {} delete file {} from fastDFS error, error info:n", user.getId(), fileId, e.getMessage());
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        //删除数据库记录
        wmMaterialMapper.deleteByPrimaryKey(dto.getId());
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult findList(WmMaterialListDto dto) {
        dto.checkParam();
        Long uid = WmThreadLocalUtils.getUser().getId();
        List<WmMaterial> datas = wmMaterialMapper.findListByUidAndStatus(dto, uid);
        datas = datas.stream().map((item) -> {
            item.setUrl(fileServerUrl + item.getUrl());
            return item;
        }).collect(Collectors.toList());
        int total = wmMaterialMapper.countListByUidAndStatus(dto, uid);
        Map<String, Object> resDatas = new HashMap<>();
        resDatas.put("curPage", dto.getPage());
        resDatas.put("size", dto.getSize());
        resDatas.put("list", datas);
        resDatas.put("total", total);
        return ResponseResult.okResult(resDatas);
    }

    @Override
    public ResponseResult changeUserMaterialStatus(WmMaterialDto dto, Short type) {
        if (dto == null || dto.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        WmUser user = WmThreadLocalUtils.getUser();
        wmMaterialMapper.updateStatusByUidAndId(dto.getId(), user.getId(), type);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
