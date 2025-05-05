package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
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


    /**
     * 保存套餐和菜品的关联关系
     * @param setmealDish
     */
    void saveDish(List<SetmealDish> setmealDish);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal getByIdWithFlavors(Long id);

    /**
     * 根据套餐id查询套餐和菜品的关联关系
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getDishFlavors(Long id);

    void deleteBatch(List<Long> ids);

    void deleteDish(List<Long> ids);

    @Select("select count(id) from dish where id = #{dishId} and status = 0")
    Integer countByDishId(Long dishId);

    @Update("update setmeal set status = #{status} where id = #{setmealId}")
    void startOrStop(Integer status, Long setmealId);

    @Select("select count(*) from (\n" +
            "select distinct s.* from setmeal s left join setmeal_dish sd on sd.setmeal_id = s.id where status = 1)a\n")
    int countSetmealByDishId(Long id);

    /**
     * 动态条件查询套餐
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询菜品选项
     * @param setmealId
     * @return
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> getDishItemBySetmealId(Long setmealId);
}
