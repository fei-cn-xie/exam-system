package com.fei.examsys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fei.examsys.entity.Banner;
import com.fei.examsys.entity.Category;
import com.fei.examsys.mapper.BannerMapper;
import com.fei.examsys.mapper.CategoryMapper;
import com.fei.examsys.mapper.QuestionMapper;
import com.fei.examsys.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    private final QuestionMapper questionMapper;

    public CategoryServiceImpl(QuestionMapper questionMapper) {
        this.questionMapper = questionMapper;
    }

    @Override
    public List<Category> findCategoryTreeList() {
        // 查询分类
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        List<Category> categories = list(queryWrapper);

        // 统计每个分类下的问题数量
        List<Map<String, Long>> cateMapList = questionMapper.selectCategoryQuestionCount();
        Map<Long, Long> cateMap = cateMapList.stream().collect(Collectors.toMap(m -> m.get("category_id"), m -> m.get("count")));
        for (Category category : categories) {
            category.setCount(cateMap.getOrDefault(category.getId(), 0L));
        }

        // 按照parent_id对分类进行分组
        Map<Long, List<Category>> longListMap = categories.stream().collect(Collectors.groupingBy(Category::getParentId));

        List<Category> parentCategoryList = categories.stream().filter(c -> c.getParentId() == 0).toList();

        // 构建树状结构
        for (Category category : parentCategoryList) {
            List<Category> sonList = longListMap.getOrDefault(category.getId(), new ArrayList<>());
            Long sumCount = sonList.stream().mapToLong(Category::getCount).sum();
            category.setCount(category.getCount() + sumCount);
            category.setChildren(sonList);
        }

        return parentCategoryList;
    }

}