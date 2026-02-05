package com.fei.examsys.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fei.examsys.entity.Banner;
import com.fei.examsys.entity.Category;
import com.fei.examsys.entity.Question;
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
        queryWrapper.eq(Category::getIsDeleted, Boolean.FALSE);
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

    @Override
    public void updateCategory(Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getParentId, category.getParentId());
        queryWrapper.ne(Category::getId, category.getId());
        queryWrapper.eq(Category::getName, category.getName());
        long count = count(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("【%s】父分类下已经存在：【%s】".formatted(category.getParentId(), category.getName()));
        }
        updateById(category);

    }

    @Override
    public void removeCategory(Long id) {
        Category category = getById(id);
        if(category == null){
            return;
        }

        LambdaUpdateWrapper<Category> wrapper = new LambdaUpdateWrapper<>();
        //1. 分类存在子分类，无法删除
        wrapper.eq(Category::getParentId, id);
        wrapper.eq(Category::getIsDeleted, Boolean.FALSE);

        long subCount = count(wrapper);
        if (subCount > 0) {
            throw new RuntimeException("%s分类有%s个子分类，请删除子分类后删除".formatted(category.getName(), subCount));
        }

        //2. 分类存在相关题目，提示先删题目
        LambdaUpdateWrapper<Question> questionWrapper = new LambdaUpdateWrapper<>();
        questionWrapper.eq(Question::getCategoryId, id);
        Long questionCount = questionMapper.selectCount(questionWrapper);
        if (questionCount > 0) {
            throw new RuntimeException("%s分类有%s个题目，请删除题目后删除该分类".formatted(category.getName(), questionCount));
        }

        // 正式删除
        wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Category::getId, id);
        wrapper.set(Category::getIsDeleted, Boolean.TRUE);
        update(wrapper);
    }

}