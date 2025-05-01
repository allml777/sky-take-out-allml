package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;

import java.util.List;

public interface SetmealService {
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void saveWithDish(SetmealDTO setmealDTO);

    SetmealDTO getByIdWithFlavors(Long id);

    void updateWithDish(SetmealDTO setmealDTO);

    void deleteBatch(List<Long> ids);
}
