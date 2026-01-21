package com.fei.examsys.service;


import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务
 * 支持MinIO和本地文件存储两种方式
 */

public interface FileUploadService {

    /**
     * 文件上传业务方法
     * @param folder 在minio中存储的文件夹（轮播图：banners，视频：videos
     * @param file 上传的文件对象
     * @return 返回的文件地址
     */
    String uploadFile(String folder, MultipartFile file);
} 