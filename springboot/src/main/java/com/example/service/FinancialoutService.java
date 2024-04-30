/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/16 23:08
 * @version 1.0
 */

package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.PowerEnum;
import com.example.common.enums.ResourcesEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.EmployeeMapper;
import com.example.mapper.FinancialinMapper;
import com.example.mapper.FinancialoutMapper;
import com.example.mapper.ResourcesMapper;
import com.example.utils.MyDateUtils;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FinancialoutService {

    @Resource
    private FinancialoutMapper financialoutMapper;

    @Resource
    private FinancialinMapper financialinMapper;
    @Resource
    private EmployeeMapper employeeMapper;
    
    @Resource
    private ResourcesMapper resourcesMapper;

    @Resource
    private DepartmentService departmentService;

    /**
     * 新增
     */
    public void add(Financialout financialout) {

        Resources resources = resourcesMapper.selectByType(ResourcesEnum.CASH_FLOW.status);
        if(financialout.getPrice() == null){
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if(resources.getPrice() == null){
            throw new CustomException(ResultCodeEnum.SYSTEM_ERROR);
        }
        if(resources.getPrice() < financialout.getPrice()){
            throw new CustomException(ResultCodeEnum.CASH_FLOW_NO_ENOUGH);
        }
        resources.setPrice(resources.getPrice() - financialout.getPrice());
        financialoutMapper.insert(financialout);
        resourcesMapper.updateById(resources);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        Financialout financialout = financialoutMapper.selectById(id);
        Resources resources = resourcesMapper.selectByType(ResourcesEnum.CASH_FLOW.status);
        resources.setPrice(resources.getPrice() - financialout.getPrice());
        resourcesMapper.updateById(resources);
        financialoutMapper.deleteById(id);
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
    public void updateById(Financialout financialout) {
        Financialout financialout1 = financialoutMapper.selectById(financialout.getId());
        Resources resources = resourcesMapper.selectByType(ResourcesEnum.CASH_FLOW.status);
        //支出变多了
        if(financialout.getPrice() > financialout1.getPrice()){
            resources.setPrice(resources.getPrice() - financialout.getPrice() + financialout1.getPrice());
        }
        //变少了
        else if(financialout.getPrice() < financialout1.getPrice()){
            resources.setPrice(resources.getPrice() + financialout.getPrice() - financialout1.getPrice());
        }
        resourcesMapper.updateById(resources);
        financialoutMapper.updateById(financialout);
    }

    /**
     * 根据ID查询
     */
    public Financialout selectById(Integer id) {
        return financialoutMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Financialout> selectAll(Financialout financialout) {
        return financialoutMapper.selectAll(financialout);
    }

    /**
     * 分页查询
     */
    public PageInfo<Financialout> selectPage(Financialout financialout, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            //普通员工
            Employee employee = employeeMapper.selectById(currentUser.getId());
            Department department = departmentService.selectById(employee.getDepartmentId());
            if(!PowerEnum.DEPARTMENT_FINANCIAL.status.equals(department.getPowerName())){
                financialout.setDepartmentId(employee.getDepartmentId());

            }
            //财务和管理员不做处理
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Financialout> list = financialoutMapper.selectAll(financialout);
        return PageInfo.of(list);
    }

    public List<Financialout> selectAll2() {
        return financialoutMapper.selectAll2();
    }

    public Map<String, Object> lineInOut() {
        List<String> recentYearMonths = MyDateUtils.generateRecentYearMonths(); // 生成最近一年的月份列表的方法，根据实际情况实现
        List<Financialin> listin = financialinMapper.selectAll(null);
        List<Financialout> listout = financialoutMapper.selectAll(null);
        // 合并收入和支出的数据，并按月份进行分组
        Map<String, Double> netIncomeMap = new TreeMap<>();
        listin.forEach(income -> netIncomeMap.merge(income.getTime().substring(0, 7), income.getPrice(), Double::sum));
        listout.forEach(outcome -> netIncomeMap.merge(outcome.getTime().substring(0, 7), -outcome.getPrice(), Double::sum));

        // 使用最近一年的月份列表作为基准，确保每个月份都有对应的收入和支出数据
        Map<String, Double> sortedNetIncomeMap = new TreeMap<>();
        recentYearMonths.forEach(month -> sortedNetIncomeMap.put(month, netIncomeMap.getOrDefault(month, 0.0)));

        // 将数据转换为前端需要的格式
        List<String> xAxis = new ArrayList<>(sortedNetIncomeMap.keySet());
        List<Double> data = new ArrayList<>(sortedNetIncomeMap.values());

        Map<String, Object> result = new HashMap<>();
        result.put("text", "公司净利润折线图");
        result.put("subtext", "统计维度：年月");
        result.put("xAxis", xAxis);
        result.put("yAxis", data);

        return result;
    }


    public Map<String, Object> pieOut() {
        // 获取所有的财务信息
        List<Financialout> list = financialoutMapper.selectAll(null);
        Map<String, Double> collect = list.stream().filter(x -> ObjectUtil.isNotEmpty(x.getDepartmentName()))
                .collect(Collectors.groupingBy(Financialout::getDepartmentName, Collectors.reducing(0.0, Financialout::getPrice, Double::sum)));
        List<Map<String, Object>> data = new ArrayList<>();
        for (String key : collect.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", key);
            map.put("value", collect.get(key));
            data.add(map);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("text", "财务支出饼图");
        result.put("subtext", "统计维度：部门");
        result.put("name", "财务支出");
        result.put("data", data);
        return result;
    }
}