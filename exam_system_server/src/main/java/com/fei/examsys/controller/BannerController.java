package com.fei.examsys.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fei.examsys.common.Result;
import com.fei.examsys.entity.Banner;
import com.fei.examsys.entity.BaseEntity;
import com.fei.examsys.service.BannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 轮播图控制器 - 处理轮播图管理相关的HTTP请求
 * 包括图片上传、轮播图的CRUD操作、状态切换等功能
 */
@RestController  // REST控制器，返回JSON数据
@RequestMapping("/api/banners")  // 轮播图API路径前缀
@CrossOrigin  // 允许跨域访问
@Tag(name = "轮播图管理", description = "轮播图相关操作，包括图片上传、轮播图增删改查、状态管理等功能")  // Swagger API分组
@Slf4j
public class BannerController {

    @Autowired
    private BannerService bannerService;
    
    /**
     * 上传轮播图图片
     * @param file 图片文件
     * @return 图片访问URL
     */
    @PostMapping("/upload-image")  // 处理POST请求
    @Operation(summary = "上传轮播图图片", description = "将图片文件上传到MinIO服务器，返回可访问的图片URL")  // API描述
    public Result<String> uploadBannerImage(
            @Parameter(description = "要上传的图片文件，支持jpg、png、gif等格式，大小限制5MB") 
            @RequestParam("file") MultipartFile file) {

        return Result.success("上传图片地址", "图片上传成功");
    }
    
    /**
     * 获取启用的轮播图（前台首页使用）
     * @return 轮播图列表
     */
    @GetMapping("/active")  // 处理GET请求
    @Operation(summary = "获取启用的轮播图", description = "获取状态为启用的轮播图列表，供前台首页展示使用")  // API描述
    public Result<List<Banner>> getActiveBanners() {
        LambdaQueryWrapper<Banner> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Banner::getIsActive, true);
        List<Banner> result = bannerService.list(queryWrapper);
        return Result.success(result);
    }
    
    /**
     *
     * 获取所有轮播图（管理后台使用）
     * @return 轮播图列表
     */
    @GetMapping("/list")  // 处理GET请求
    @Operation(summary = "获取所有轮播图", description = "获取所有轮播图列表，包括启用和禁用的，供管理后台使用")  // API描述
    public Result<List<Banner>> getAllBanners() {
        // LEARN
        //  1. 解决排序问题
        //  2. 解决逻辑删除问题
        //  3. 解决某些字段无需返回但返回了： springmvc中jackson处理的，所以更改jackson配置即可。在实体类的需要忽略的属性上加@JsonIgnore()即可
        //  4. 解决时区问题：方案一：使用jackson注解@JsonFormat()进行局部配置 方案二：全局配置：application.yaml中spring.jackson.data-format/time-zone
        LambdaQueryWrapper<Banner> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Banner::getIsDeleted, 0);
        queryWrapper.orderByAsc(Banner::getSortOrder);
        List<Banner> result = bannerService.list(queryWrapper);
        log.info("后台管理部分查询所有轮播图成功! 查询数量为: {}, 查询结果为:{}", result.size(), result);
        return Result.success(result);
    }
    
    /**
     * 根据ID获取轮播图
     * @param id 轮播图ID
     * @return 轮播图详情
     */
    @GetMapping("/{id}")  // 处理GET请求
    @Operation(summary = "根据ID获取轮播图", description = "根据轮播图ID获取单个轮播图的详细信息")  // API描述  
    public Result<Banner> getBannerById(@Parameter(description = "轮播图ID") @PathVariable Long id) {
        LambdaQueryWrapper<Banner> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BaseEntity::getId, id);
        Banner one = bannerService.getOne(queryWrapper);

        return Result.success(one);
    }
    
    /**
     * 添加轮播图
     * @param banner 轮播图对象
     * @return 操作结果
     */
    @PostMapping("/add")  // 处理POST请求
    @Operation(summary = "添加轮播图", description = "创建新的轮播图，需要提供图片URL、标题、跳转链接等信息")  // API描述
    public Result<String> addBanner(@RequestBody Banner banner) {
        return null;
    }
    
    /**
     * 更新轮播图
     * @param banner 轮播图对象
     * @return 操作结果
     */
    @PutMapping("/update")  // 处理PUT请求
    @Operation(summary = "更新轮播图", description = "更新轮播图的信息，包括图片、标题、跳转链接、排序等")  // API描述
    public Result<String> updateBanner(@RequestBody Banner banner) {
        LambdaUpdateWrapper<Banner> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Banner::getId, banner.getId());
        updateWrapper.setEntity(banner);
        bannerService.update(updateWrapper);
        log.info("更新轮播图数据成功{}", banner);
        return Result.success(null);
    }
    
    /**
     * LEARN 删除功能需要考虑的问题
     *   1. 是否做了逻辑删除
     *   2. 有没有重要的关联数据，不能删除： 比如 试卷表 - 题目表 如果有试卷引用题目，就不能删除题目
     *   3. 删除时，顺便删除子数据： 题目表 => 选项表 => 答案表 ： 删除题目 ==> 删除对应的选项和答案
     * 删除轮播图
     * @param id 轮播图ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")  // 处理DELETE请求
    @Operation(summary = "删除轮播图", description = "根据ID删除指定的轮播图")  // API描述
    public Result<String> deleteBanner(@Parameter(description = "轮播图ID") @PathVariable Long id) {
        LambdaUpdateWrapper<Banner> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Banner::getId, id);
        updateWrapper.set(Banner::getIsDeleted, Boolean.TRUE);
        bannerService.update(updateWrapper);
        return Result.success("删除成功");
    }
    
    /**
     * 启用/禁用轮播图
     * @param id 轮播图ID
     * @param isActive 是否启用
     * @return 操作结果
     */
    @PutMapping("/toggle/{id}")  // 处理PUT请求
    @Operation(summary = "切换轮播图状态", description = "启用或禁用指定的轮播图，禁用后不会在前台显示")  // API描述
    public Result<String> toggleBannerStatus(
            @Parameter(description = "轮播图ID") @PathVariable Long id, 
            @Parameter(description = "是否启用，true为启用，false为禁用") @RequestParam Boolean isActive) {
        LambdaUpdateWrapper<Banner> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Banner::getId, id);
        updateWrapper.set(Banner::getIsActive, isActive);
        bannerService.update(updateWrapper);
        log.info("id = {} 轮播图，状态修改成功！修改后的状态为 isActive = {}", id, isActive);
        return Result.success(null);

    }
} 