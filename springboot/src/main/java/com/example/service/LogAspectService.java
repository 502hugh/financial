/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/4/16 12:56
 * @version 1.0
 */

package com.example.service;

import com.example.entity.Employee;
import com.example.mapper.LogAspectMapper;
import com.example.entity.Aspect.Log;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LogAspectService {


    @Resource
    private  LogAspectMapper logAspectMapper;
    public void save(Log log) {
        logAspectMapper.insert(log);
    }

    public PageInfo<Log> selectPage(Log log, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Log> list = logAspectMapper.selectAll(log);
        return PageInfo.of(list);
    }

    public void deleteById(Integer id) {
        logAspectMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (int i : ids){
            deleteById(i);
        }
    }

    public List<Log> selectAll(Log log) {
        List<Log> logs = logAspectMapper.selectAll(log);
        return logs;
    }
}
