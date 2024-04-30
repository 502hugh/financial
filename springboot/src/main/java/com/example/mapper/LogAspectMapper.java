package com.example.mapper;


import com.example.entity.Aspect.Log;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LogAspectMapper {
    void insert(Log log);

    List<Log> selectAll(Log log);


    void deleteById(Integer id);
}
