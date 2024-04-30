/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/16 21:25
 * @version 1.0
 */

package com.example.service;

import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Department;
import com.example.entity.Employee;
import com.example.exception.CustomException;
import com.example.mapper.DepartmentMapper;
import com.example.mapper.EmployeeMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DepartmentService {

    @Resource
    private DepartmentMapper departmentMapper;

    @Resource
    private EmployeeMapper employeeMapper;

    @Resource
    private EmployeeService employeeService;

    /**
     * 新增
     */
    public void add(Department department) {
        //部门权限问题
        if(department.getPowerName() != null){
            Department department1 = departmentMapper.selectByPowerName(department.getPowerName());
            //该权限已有部门归属
            if(department1 != null){
                throw  new CustomException(ResultCodeEnum.EMPLOYEE_HAVE_POWERNAME_ERORR);
            }
        }

        //新增加部门的时候，选了主管
        if(department.getEmployeeId() != null){
            //找出选择的部长，判断原来是不是主管
            Employee employee = employeeMapper.selectById(department.getEmployeeId());
            if(employee.getLevel() == 2 ){
                throw  new CustomException(ResultCodeEnum.EMPLOYEE_HAVE_DEPARTMENT_ERORR);
            }else{
                //如果该员工不是部长，那么新增，先新增部门，获取部门id,修改员工的部门id
                departmentMapper.insert(department);
                department = departmentMapper.selectByName(department.getName());
                employee.setDepartmentId(department.getId());
                employee.setLevel(2);
                employeeMapper.updateById(employee);
            }

        }
        //新增加部门，没有选择主管
        else{
            departmentMapper.insert(department);
        }

    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {
        List<Employee> list = employeeMapper.selectByDepartmentId(id);
        for(Employee e : list){
            e.setLevel(null);
            e.setDepartmentId(null);
            employeeService.updateById(e);
        }
        departmentMapper.deleteById(id);
    }

    public void deleteEmployeeId(Employee employee) {
        Department department = departmentMapper.selectById(employee.getDepartmentId());
        department.setEmployeeId(null);
        departmentMapper.updateById(department);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            departmentMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateById(Department department) {
        //部门权限问题
        if(department.getPowerName() != null){
            Department department1 = departmentMapper.selectByPowerName(department.getPowerName());
            //该权限已有部门归属
            if(department1 != null){
                throw  new CustomException(ResultCodeEnum.EMPLOYEE_HAVE_POWERNAME_ERORR);
            }
        }

        departmentMapper.updateById(department);
    }

    /**
     * 根据ID查询
     */
    public Department selectById(Integer id) {
        return departmentMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Department> selectAll(Department department) {
        return departmentMapper.selectAll(department);
    }

    /**
     * 分页查询
     */
    public PageInfo<Department> selectPage(Department department, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Department> list = departmentMapper.selectAll(department);
        return PageInfo.of(list);
    }

    public Department selectByName(String name) {
        return departmentMapper.selectByName(name);
    }

    public Department selectByEmployeeId(Integer id) {
        return departmentMapper.selectByEmployeeId(id);
    }

    public Department selectByPowerName(String powerName) {
        return departmentMapper.selectByPowerName(powerName);
    }
}