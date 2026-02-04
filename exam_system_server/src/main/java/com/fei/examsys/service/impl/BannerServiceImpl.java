package com.fei.examsys.service.impl;

import com.fei.examsys.entity.Banner;
import com.fei.examsys.mapper.BannerMapper;
import com.fei.examsys.service.BannerService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 轮播图服务实现类
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    private final FileUploadServiceImpl fileUploadServiceImpl;

    public BannerServiceImpl(FileUploadServiceImpl fileUploadServiceImpl) {
        this.fileUploadServiceImpl = fileUploadServiceImpl;
    }

    @Override
    public String uploadBannerImage(MultipartFile file) {
        // 1. 文件非空校验
        if (file.isEmpty()) {
            throw new RuntimeException("上传的轮播图文件为空！上传失败");
        }
        // 2. 文件类型校验
        if(file.getContentType() == null ||  !file.getContentType().startsWith("image")){
            throw new RuntimeException("上传的轮播图文件类型错误! 上传失败");
        }
        // 3. 调用文件上传业务
        String imageUrl = fileUploadServiceImpl.uploadFile("banners", file);
        // 4. 返回文件链接
        return imageUrl;
    }
}