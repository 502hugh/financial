package com.example.mapper;

import com.example.entity.Visible;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface VisibleMapper {
    
    
    void updateById(Visible visible);

    Visible selectById(Integer id);
}
