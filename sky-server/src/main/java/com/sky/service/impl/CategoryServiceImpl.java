package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.sky.constant.MessageConstant;
import com.sky.exception.DeletionNotAllowedException;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        log.info("分页查询结果：{}", page);
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        log.info("impl中的id & status{}{}",id,status);
        Category category = Category.builder()
                .status(status)
                .id(id)
                .updateTime(LocalDateTime.now())
                .build();
        categoryMapper.update(category);
    }

    @Override
    public void update(CategoryDTO categoryDTO) {
        log.info("impl中的categoryDTO{}",categoryDTO);
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.update(category);
    }

    @Override
    public void save(CategoryDTO categoryDTO) {
        log.info("impl中的categoryDTO{}",categoryDTO);

        Category category = new Category();

        BeanUtils.copyProperties(categoryDTO, category);

        category.setStatus(StatusConstant.DISABLE);

        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.save(category);
    }

    @Override
    public void deleteById(Long id) {
        log.info("删除分类中的id{}",id);
        //查询当前分类是否关联了菜品，如果关联了就抛出业务异常
        Integer count = dishMapper.countByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //查询当前分类是否关联了套餐，如果关联了就抛出业务异常
        count = setmealMapper.countByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        categoryMapper.deleteById(id);
    }

    @Override
    public List<Category> list(Integer type) {
        log.info("查询分类");
        return categoryMapper.list(type);
    }
}
