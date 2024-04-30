package com.example.mapper;

import com.example.entity.Financialin;
import com.example.entity.Financialin;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface FinancialinMapper {
    /**
     * 新增
     */
    int insert(Financialin financialin);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Financialin financialin);

    /**
     * 根据ID查询
     */
    Financialin selectById(Integer id);

    /**
     * 查询所有
     */
    List<Financialin> selectAll(Financialin financialin);

    @Select("select left(time,7) as time,price from financialin")
    List<Financialin> selectAll2();


}
