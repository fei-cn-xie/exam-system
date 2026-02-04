package com.fei.examsys.service;

import com.fei.examsys.entity.Banner;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

/**
 * 轮播图服务接口
 */
public interface BannerService extends IService<Banner> {

    /**
     * 轮播图文件上传
     * @param file
     * @return
     */
    String uploadBannerImage(MultipartFile file);
}