package com.example.mapper;

import com.example.entity.Financialout;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FinancialoutMapper {
    /**
     * 新增
     */
    int insert(Financialout financialout);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Financialout financialout);

    /**
     * 根据ID查询
     */
    Financialout selectById(Integer id);

    /**
     * 查询所有
     */
    List<Financialout> selectAll(Financialout financialout);

    @Select("select left(time,7) as time,price from financialout")
    List<Financialout> selectAll2();
}


