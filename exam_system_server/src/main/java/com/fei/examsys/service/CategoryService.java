package com.fei.examsys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.fei.examsys.entity.Category;

import java.util.List;


public interface CategoryService extends IService<Category> {
    List<Category> findCategoryTreeList();
}