/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/22 13:43
 * @version 1.0
 */

package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ResourcesEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Employee;
import com.example.entity.Financialin;
import com.example.entity.Resources;
import com.example.exception.CustomException;
import com.example.mapper.EmployeeMapper;
import com.example.mapper.FinancialinMapper;
import com.example.mapper.ResourcesMapper;
import com.example.utils.MyDateUtils;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FinancialinService {

    @Resource
    private FinancialinMapper financialinMapper;
    @Resource
    private EmployeeMapper employeeMapper;


    @Resource
    private ResourcesMapper resourcesMapper;
    /**
     * 新增
     */
    public void add(Financialin financialin) {
        //判断参数
        if(financialin.getType() == null && financialin.getPrice() == null && financialin.getTime() == null){
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        //资产信息对象
        if(ResourcesEnum.CASH_FLOW.status.equals(financialin.getType())){
            Resources resources1 = resourcesMapper.selectByType(financialin.getType());
            resources1.setPrice(resources1.getPrice() + financialin.getPrice());
            financialinMapper.insert(financialin);
            resourcesMapper.updateById(resources1);
        }
        else{
            Resources resources1 = resourcesMapper.selectByTypeAndName(financialin.getType(),financialin.getName());
            if(resources1 != null){
                resources1.setNum(resources1.getNum() + financialin.getResourcesNum());
                financialinMapper.insert(financialin);
                resourcesMapper.updateById(resources1);
            }
            else{
                Resources resources = new Resources();
                resources.setNum(financialin.getResourcesNum());
                resources.setName(financialin.getResourcesName());
                resources.setType(financialin.getType());
                resources.setPrice(financialin.getPrice());
                financialinMapper.insert(financialin);
                resourcesMapper.insert(resources);
            }
        }
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        Financialin financialin = financialinMapper.selectById(id);
        if(ResourcesEnum.CASH_FLOW.status.equals(financialin.getType())){
            Resources resources = resourcesMapper.selectByType(financialin.getType());
            resources.setPrice(resources.getPrice() - financialin.getPrice());
            resourcesMapper.updateById(resources);
        }
        financialinMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Financialin financialin) {


        //判断参数
        if(financialin.getType() == null && financialin.getPrice() == null && financialin.getTime() == null){
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        //目前不考虑非现金流的情况
        //资产信息对象
        if(ResourcesEnum.CASH_FLOW.status.equals(financialin.getType())){
            Financialin financialin1 = financialinMapper.selectById(financialin.getId());
            Resources resources = resourcesMapper.selectByType(financialin.getType());
            //钱变多了
            if(financialin.getPrice() > financialin1.getPrice()){
                resources.setPrice(resources.getPrice() + financialin.getPrice() - financialin1.getPrice());
            }
            //变少了
            else if(financialin.getPrice() < financialin1.getPrice()){
                resources.setPrice(resources.getPrice() - financialin.getPrice() + financialin1.getPrice());
            }
            resourcesMapper.updateById(resources);
        }
        financialinMapper.updateById(financialin);
    }

    /**
     * 根据ID查询
     */
    public Financialin selectById(Integer id) {
        return financialinMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Financialin> selectAll(Financialin financialin) {
        return financialinMapper.selectAll(financialin);
    }

    /**
     * 分页查询
     */
    public PageInfo<Financialin> selectPage(Financialin financialin, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<Financialin> list = financialinMapper.selectAll(financialin);
        return PageInfo.of(list);
    }

    public List<Financialin> selectAll2() {
        return financialinMapper.selectAll2();
    }

    public Map<String, Object> barIn() {
        // 获取所有的财务支出信息
        List<Financialin> list = financialinMapper.selectAll2();

        // 使用 TreeMap 对键（年月）进行排序
        TreeMap<String, Double> collect = list.stream()
                .filter(x -> ObjectUtil.isNotEmpty(x.getTime().substring(0,7)) && MyDateUtils.isWithinLastYear(x.getTime()))
                .collect(Collectors.groupingBy(Financialin::getTime, TreeMap::new, Collectors.reducing(0.0, Financialin::getPrice, Double::sum)));

        List<String> xAxis = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        for (String key : collect.keySet()) {
            xAxis.add(key);
            data.add(collect.get(key));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("text", "财务收入柱状");
        result.put("subtext", "统计维度：年月");
        result.put("xAxis", xAxis);
        result.put("yAxis", data);

        return result;
    }

}
