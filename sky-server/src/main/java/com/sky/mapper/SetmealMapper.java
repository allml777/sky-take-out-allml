package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.result.PageResult;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     *
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 根据菜品id查询套餐
     *
     * @param ids
     * @return
     */
    List<Long> getSetmealByDishIds(List<Long> ids);

    Page<Setmeal> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    @Insert("insert into setmeal(category_id,name,price,status,description,image,create_time,update_time,create_user,update_user)"
            + "values "
            + "(#{categoryId},#{name},#{price},#{status},#{description},#{image},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void saveSetmeal(Setmeal setmeal);


    void saveDish(List<SetmealDish> setmealDish);

    @Select("select * from setmeal where id = #{id}")
    Setmeal getByIdWithFlavors(Long id);

    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getDishFlavors(Long id);

    void deleteBatch(List<Long> ids);

    void deleteDish(List<Long> ids);
}
