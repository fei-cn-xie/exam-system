package com.fei.examsys.service.impl;

import com.fei.examsys.config.properties.MinioProperties;
import com.fei.examsys.service.FileUploadService;
import io.minio.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * projectName: com.fei.examsys.service.impl
 *
 * @author: 赵伟风
 * description:
 */
@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    MinioClient minioClient;

    @Autowired
    MinioProperties minioProperties;

    @SneakyThrows // LEARN 向上层抛出异常的简化写法
    @Override
    public String uploadFile(String folder, MultipartFile file) {
        // 1. 链接minio客户端
        // 2. 判断桶是否存在
        boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getBucketName()).build());
        // 3. 不存在，则创建桶，并设置访问权限
        if (!bucketExists) {
            String policy = """
                    {
                        "Version": "2012-10-17",
                        "Statement": [
                            {
                                "Effect": "Allow",
                                "Principal": {
                                    "AWS": [
                                        "*"
                                    ]
                                },
                                "Action": [
                                    "s3:GetObject"
                                ],
                                "Resource": [
                                    "arn:aws:s3:::%s/*"
                                ]
                            }
                        ]
                    }""".formatted(minioProperties.getBucketName()); // LEARN 通过%s占位符写入桶名
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucketName()).build());
            minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .config(policy)
                    .build());
        }
        // 4. 上传文件
        // 在minio中创建一个folder的文件夹，并将文件放在此文件夹中
        String fileName = folder + '/' + file.getOriginalFilename();
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(minioProperties.getBucketName())
                .contentType(file.getContentType())
                .object(fileName)
                // LEARN stream指上传文件的输入流数据
                //  参数1：文件输入流
                //  参数2： 文件字节大小
                //  参数3： 是否切割文件，转多线程上传 1000，100，-1代表自动判断切割大小
                .stream(file.getInputStream(), file.getSize(), -1)
                .build());

        // 5. 拼接访问地址
        // url = endpoint + '/' + bucket + '/" + fileName
        String url = String.join("/", minioProperties.getEndpoint(), minioProperties.getBucketName(),fileName);
        log.info("文件上传成功，文件地址为：{}", url);
        return url;

    }
}
