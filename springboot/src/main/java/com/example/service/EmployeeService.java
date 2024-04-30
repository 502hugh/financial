/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/14 22:54
 * @version 1.0
 */

package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.DepartmentMapper;
import com.example.mapper.EmployeeMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
public class EmployeeService {

    @Resource
    private EmployeeMapper employeeMapper;

    @Resource
    private DepartmentService departmentService;
    @Resource
    private SalaryManagerService salaryManagerService;

    @Resource
    private DepartmentMapper departmentMapper;


    @Resource
    private UserService userService;

    /**
     * 新增
     *
     * @return
     */

    public String addByUp(Employee employee) {
        Employee dbEmployee = employeeMapper.selectByUsername(employee.getUsername());
        if (ObjectUtil.isNotNull(dbEmployee)) {
            return dbEmployee.getUsername();
        }
        if(ObjectUtil.isEmpty(employee.getLevel()) && ObjectUtil.isEmpty(employee.getDepartmentName())){
            add(employee);
        }else if(ObjectUtil.isNotEmpty(employee.getLevel()) && ObjectUtil.isNotEmpty(employee.getDepartmentName())){
            //先判断部门有没有
            Department department = departmentService.selectByName(employee.getDepartmentName());
            if(department!=null){
                if(employee.getLevel() == 2 && department.getEmployeeId()!=null){
                    return "部门已经有部长";
                }
                add(employee);
            }
        }
        return null;
    }
    @Transactional(rollbackFor = Exception.class)
    public void add(Employee employee) {
        Employee dbEmployee = employeeMapper.selectByUsername(employee.getUsername());
        if (ObjectUtil.isNotNull(dbEmployee)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        if (ObjectUtil.isEmpty(employee.getPassword())) {
            employee.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        if (ObjectUtil.isEmpty(employee.getName())) {
            employee.setName(Constants.NAME);
        }
        // 初始化一个角色
        employee.setRole(RoleEnum.USER.name());
        //部门和员工级别的判断
        employeeMapper.insert(employee);
        int id = employeeMapper.selectByUsername(employee.getUsername()).getId();
        //创建角色的工资信息
        SalaryManager salaryManager = salaryManagerService.setSalaryManager(employee);
        salaryManager.setEmployeeId(id);
        salaryManagerService.add(salaryManager);
        //
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {

        Employee employee = employeeMapper.selectById(id);
        if(employee.getLevel() != null && employee.getLevel() == 2){
            //删除部门表
            departmentService.deleteEmployeeId(employee);
        }
        //删除用户表
        userService.deleteByRoleid(userService.getRoleid(employee));
        //删除薪资管理表
        salaryManagerService.deleteByEmployeeId(id);
        //删除用户
        employeeMapper.deleteById(id);
    }

    /**
     * 批量删除
     * 暂时不用
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            salaryManagerService.deleteByEmployeeId(id);
            employeeMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateById(Employee employee) {
        // 检查员工的级别和部门ID是否同时为空或者同时不为空，如果是，则抛出异常
        if ((employee.getLevel() == null && employee.getDepartmentId() != null) ||
                (employee.getLevel() != null && employee.getDepartmentId() == null)) {
            throw new CustomException(ResultCodeEnum.EMPLOYEE_D_L_ERROR);
        }
        // 根据员工ID查找原先的级别和部门ID
        Employee employee_o = employeeMapper.selectById(employee.getId());
        Integer originalLevel = employee_o.getLevel();
        Integer originalDepartmentId = employee_o.getDepartmentId();
        // 不涉及部门的更新
        if (employee.getLevel() == null && employee.getDepartmentId() == null) {
            employeeMapper.updateById(employee);
            User user = userService.change(employee);
            userService.updateByRoleid(user);
        }
        else {
            // 根据员工的部门ID查找新部门信息
            Department department = departmentService.selectById(employee.getDepartmentId());


            System.out.println("============================================"+
                 "\n新部门：" +  department.getId() +
                    "\n新等级：" + employee.getLevel() +
                     "\n旧部门：" +  originalDepartmentId +
                      "\n旧等级：" + originalLevel  );
            //如果要当部长
            if(employee.getLevel() == 2){
                //如果部门没有改变
                if(Objects.equals(employee.getDepartmentId(), originalDepartmentId)){
                    if (department.getEmployeeId() != null){//部门原来有部长
                        if(originalLevel == 1){
                            throw new CustomException(ResultCodeEnum.EMPLOYEE_HAVE_LEADER_ERROR);
                        }
                    }
                    else{
                        //部门原来没有部长
                        //员工原来是部长,情况不成立，这是在同一个部门
                        //员工原来不是部长
                        if(originalLevel == 1){
                            department.setEmployeeId(employee.getId());
                            departmentMapper.updateById(department);
                        }
                    }
                }
                else{ //部门发生改变
                    //新部门原来有部长
                    if (department.getEmployeeId() != null){
                        throw new CustomException(ResultCodeEnum.EMPLOYEE_HAVE_LEADER_ERROR);
                    }
                    //新部门原来没有部长
                    else{
                        //员工原来是部长,现在成为另一个部门部长，修改原来部门的id为null，新部门id为员工id
                        if(originalLevel == 2) {
                            department.setEmployeeId(employee.getId());
                            departmentMapper.updateById(department);
                            //根据旧部门id查找旧部门信息
                            Department department1 = departmentMapper.selectById(originalDepartmentId);
                            department1.setEmployeeId(null);
                            departmentMapper.updateById(department1);
                        }
                        //员工原来不是部长,现在成为另一个部门部长,新部门id为员工id
                        if(originalLevel == 1){
                            department.setEmployeeId(employee.getId());
                            departmentMapper.updateById(department);
                        }
                    }
                }

            }
            else{//如果不是当部长
                //原来是部长
                if(originalLevel != null && originalLevel == 2){
                    department.setEmployeeId(null);
                    //更新部门信息
                    departmentMapper.updateById(department);
                }
            }

            employeeMapper.updateById(employee);
            User user = userService.change(employee);
            userService.updateByRoleid(user);
        }


        //修改工资情况
        SalaryManager salaryManager = salaryManagerService.updateSalaryManager(employee);
        salaryManagerService.updateById(salaryManager);
    }

    /**
     * 根据ID查询
     */
    public Employee selectById(Integer id) {
        return employeeMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Employee> selectAll(Employee employee) {
        return employeeMapper.selectAll(employee);
    }


    public Employee selectByUsername(String username) {
        return employeeMapper.selectByUsername(username);
    }

    /**
     * 分页查询
     */
    public PageInfo<Employee> selectPage(Employee employee, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        //员工
        /**
         * 1、如果是普通员工，只能查找自己的信息
         * 2、如果是部长，可以查询到自己部门的信息
         * 3、如果是人事部门的，跟管理员一样
         */
//        if(RoleEnum.USER.name().equals(currentUser.getRole())){
//            Employee employee1 = employeeMapper.selectById(currentUser.getId());
//            Department department = departmentService.selectById(employee1.getDepartmentId());
//
//            if(employee1.getLevel() == 1){
//                employee.setId(employee1.getId());
//                employee.setDepartmentId(employee1.getDepartmentId());
//            }
//            //除了人事部门外  部长一类的人
//            if(!PowerEnum.DEPARTMENT_HUMAN.status.equals(department.getPowerName()) && employee1.getLevel() == 2){
//                employee.setDepartmentId(employee1.getDepartmentId());
//            }
//        }
        //管理员不做处理查询所有
        PageHelper.startPage(pageNum, pageSize);
        List<Employee> list = employeeMapper.selectAll(employee);
        return PageInfo.of(list);
    }

    /**
     * 登录
     */
    public Account login(Account account) {
        Account dbEmployee = employeeMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbEmployee)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbEmployee.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        // 生成token


        String tokenData = dbEmployee.getId() + "-" + RoleEnum.USER.name();
        String token = TokenUtils.createToken(tokenData, dbEmployee.getPassword());
        dbEmployee.setToken(token);
//        System.out.println("============================================================================================");
//        System.out.println(tokenData);
        return dbEmployee;
    }

    /**
     * 注册
     */
    public void register(Account account) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(account, employee);
        add(employee);
    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        Employee dbEmployee = employeeMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbEmployee)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbEmployee.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbEmployee.setPassword(account.getNewPassword());
        employeeMapper.updateById(dbEmployee);
    }


    public List<Employee> selectHeaders() {
        return employeeMapper.selectHeaders();
    }


    public Employee selectByIdPower() {
        Account currentUser = TokenUtils.getCurrentUser();
        Employee employee = employeeMapper.selectById(currentUser.getId());
        return employeeMapper.selectByIdPower(employee);
    }

    public void updateById1(Employee employee) {
        //修改工资情况
        SalaryManager salaryManager = salaryManagerService.updateSalaryManager(employee);
        salaryManagerService.updateById(salaryManager);
        employeeMapper.updateById(employee);
    }


}
