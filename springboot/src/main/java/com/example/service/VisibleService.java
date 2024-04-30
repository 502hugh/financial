/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/4/19 21:37
 * @version 1.0
 */

package com.example.service;

import com.example.entity.Visible;
import com.example.mapper.VisibleMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
public class VisibleService {

    @Resource
    private VisibleMapper visibleMapper;



    public void updateById(Visible visible) {
        visibleMapper.updateById(visible);
    }

    public Visible selectById(Integer id) {
        return visibleMapper.selectById(id);
    }
}
