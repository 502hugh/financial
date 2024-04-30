package com.example.mapper;

import com.example.entity.Resourceapply;

import java.util.List;

public interface ResourceapplyMapper {

    /**
     * 新增
     */
    int insert(Resourceapply resourceapply);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Resourceapply resourceapply);

    /**
     * 根据ID查询
     */
    Resourceapply selectById(Integer id);

    /**
     * 查询所有
     */
    List<Resourceapply> selectAll(Resourceapply resourceapply);

}
