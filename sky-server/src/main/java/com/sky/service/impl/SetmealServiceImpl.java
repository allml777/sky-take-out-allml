package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<Setmeal> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        // 获取套餐信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.saveSetmeal(setmeal);

        // 获取生成的套餐id
        Long setmealId = setmeal.getId();

        // 获取套餐内的菜品信息
        List<SetmealDish> setmealDish = setmealDTO.getSetmealDishes();
        // 为菜品设置套餐id
        setmealDish.forEach(dish -> dish.setSetmealId(setmealId));

        setmealMapper.saveDish(setmealDish);
    }

    @Transactional
    @Override
    public SetmealDTO getByIdWithFlavors(Long id) {
        SetmealDTO setmealDTO = new SetmealDTO();

        // 获取套餐信息并设置到setmealDTO中
        Setmeal setmeal = setmealMapper.getByIdWithFlavors(id);
        BeanUtils.copyProperties(setmeal, setmealDTO);

        // 获取套餐中的菜品信息并设置到setmealDTO中
        setmealDTO.setSetmealDishes(setmealMapper.getDishFlavors(id));
        return setmealDTO;
    }

    @Transactional
    @Override
    public void updateWithDish(SetmealDTO setmealDTO) {
        List<Long> setmealId = new ArrayList<>();
        setmealId.add(setmealDTO.getId());
        // 删除套餐
        deleteBatch(setmealId);

        // 添加套餐信息
        saveWithDish(setmealDTO);
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        setmealMapper.deleteBatch(ids);
        setmealMapper.deleteDish(ids);
    }

    @Override
    public void startOrStop(Integer status, Long setmealId) {
        // 判断套餐中的菜品有无停售
        List<SetmealDish> dishFlavors = setmealMapper.getDishFlavors(setmealId);
        for(SetmealDish setmealDish : dishFlavors){
            Long dishId = setmealDish.getDishId();
            if(setmealMapper.countByDishId(dishId) > 0){
                throw new RuntimeException("套餐中包含停售菜品，无法起售");
            }
        }
        // 停售套餐
        setmealMapper.startOrStop(status, setmealId);
    }
}
