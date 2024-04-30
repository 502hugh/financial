/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/16 23:35
 * @version 1.0
 */

package com.example.mapper;

import com.example.entity.Resources;

import java.util.List;

public interface ResourcesMapper {

    /**
     * 新增
     */
    int insert(Resources resources);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Resources resources);

    /**
     * 根据ID查询
     */
    Resources selectById(Integer id);

    /**
     * 查询所有
     */
    List<Resources> selectAll(Resources resources);

    Resources selectByType(String type);

    Resources selectByTypeAndName(String type, String name);
}
