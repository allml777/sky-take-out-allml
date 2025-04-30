package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /*
    @Transactional
    表示开启一个事务，要么全成功，提交事务，
    任何一个失败就回滚事务，确保数据一致性。因为这里要操作两张表，
    只操作一张表就不需要事务，执行失败会抛异常的。
    */
    @Transactional
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        // 向菜品表 dish 插入 1 条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        /*
        这里能获取到 id 是因为DishMapper的对应方法有做主键返回
        即在xml对应的insert方法中配置了useGeneratedKeys="true"和keyProperty="id"
        执行完insert之后就会返回id
         */
        Long dishId = dish.getId();

        // 向口味表 flavor 插入 n 条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            // 遍历 flavors 为每个 dishFlavor 设置 dishId
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
