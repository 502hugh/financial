/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/16 23:36
 * @version 1.0
 */

package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.PowerEnum;
import com.example.common.enums.ResourcesEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.DepartmentMapper;
import com.example.mapper.FinancialoutMapper;
import com.example.mapper.ResourcesMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ResourcesService {

    @Resource
    private ResourcesMapper resourcesMapper;


    @Resource
    private DepartmentService departmentService;

    @Resource
    private DepartmentMapper departmentMapper;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private FinancialoutMapper financialoutMapper;

    /**
     * 新增
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(Resources resources) {
        double v = resources.getPrice() * resources.getNum();
        Financialout financialout = new Financialout();
        financialout.setPrice(v);
        financialout.setTime(DateUtil.now());
        financialout.setName("采购"+resources.getType() + resources.getName());
        financialout.setDepartmentId(departmentMapper.selectByPowerName(PowerEnum.DEPARTMENT_PURCHASE.status).getId());
        financialoutMapper.insert(financialout);
        resourcesMapper.insert(resources);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        resourcesMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            resourcesMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Resources resources) {
        resourcesMapper.updateById(resources);
    }

    /**
     * 根据ID查询
     */
    public Resources selectById(Integer id) {
        return resourcesMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Resources> selectAll(Resources resources) {
        return resourcesMapper.selectAll(resources);
    }

    /**
     * 分页查询
     */
    public PageInfo<Resources> selectPage(Resources resources, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Account currentUser = TokenUtils.getCurrentUser();
        if(RoleEnum.USER.name().equals(currentUser.getRole())){
            Employee employee = employeeService.selectById(currentUser.getId());
            Department department = departmentService.selectById(employee.getDepartmentId());
            if(department == null){
                throw new CustomException(ResultCodeEnum.EMPLOYEE_NO_DEPARTMENT);
            }
            if(!PowerEnum.DEPARTMENT_FINANCIAL.status.equals(department.getPowerName()) ){
                resources.setType(ResourcesEnum.FIXED_ASSETS.status);
            }
        }
        List<Resources> list = resourcesMapper.selectAll(resources);
        return PageInfo.of(list);
    }

    public Resources selectByType(String type) {
        return resourcesMapper.selectByType(type);
    }
}