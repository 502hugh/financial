/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/4/3 23:00
 * @version 1.0
 */

package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.PowerEnum;
import com.example.common.enums.ResourcesEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.*;
import com.example.utils.ScannerUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SalaryManagerService {

    @Resource
    private SalaryManagerMapper salaryManagerMapper;

    @Resource
    private EmployeeService employeeService;
    @Resource
    private SalaryMapper salaryMapper;

    @Resource
    private DepartmentMapper departmentMapper;


    @Resource
    private FinancialoutMapper financialoutMapper;
    @Resource
    private ResourcesMapper resourcesMapper;

    public void deleteById(Integer id) {
        salaryManagerMapper.deleteById(id);
    }

    public PageInfo<SalaryManager> selectPage(SalaryManager salaryManager, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if(salaryManager.getEmployeeName() != null) {
            Employee employee = employeeService.selectByUsername(salaryManager.getEmployeeName());
            if(employee != null)
                salaryManager.setEmployeeId(employee.getId());
        }
        List<SalaryManager> list = salaryManagerMapper.selectAll(salaryManager);
        return PageInfo.of(list);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateById(SalaryManager salaryManager) {
        if (salaryManager.getEmployeeId() == null || salaryManager.getBasicSalary() == null || salaryManager.getPositionSalary() == null) {
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        //计算所得税收
        double sum = this.scanner(salaryManager);
        salaryManager.setPersonalIncomeTax(ScannerUtils.scannerCount(sum));
        salaryManagerMapper.updateById(salaryManager);
    }

    public void add(SalaryManager salaryManager) {
        if (salaryManager.getEmployeeId() == null || salaryManager.getBasicSalary() == null || salaryManager.getPositionSalary() == null) {
            System.out.println(salaryManager.toString());
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        //计算所得税收
        double sum = this.scanner(salaryManager);
        salaryManager.setPersonalIncomeTax(ScannerUtils.scannerCount(sum));
        salaryManagerMapper.insert(salaryManager);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Integer> ids) {
        for (int i : ids){
            salaryManagerMapper.deleteById(i);
        }
    }

    public SalaryManager selectByEmployeeId(Integer employeeId) {
        SalaryManager salaryManager = salaryManagerMapper.selectByEmployeeId(employeeId);
        return salaryManager;
    }


    @Transactional(rollbackFor = Exception.class)
    public void grant(SalaryManager salaryManager) {
        //查询出所有的员工的工资
        //employeeId salary salaryManager.date
        List<Salary> salaryList = salaryManagerMapper.selectSalary();
        if(salaryManager.getSalaryDate() == null){
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if(salaryList == null){
            throw new CustomException(ResultCodeEnum.SALARY_MANAGER_NO_RECORD);
        }
        //新增到工资表,算出总的支出薪资
        Double sum = 0.0;
        for(Salary s : salaryList){
            if(s.getPrice() == null){
                throw new CustomException(ResultCodeEnum.SALARY_MANAGER_GETPRICE_RECORD);
            }
            sum += s.getPrice();
            s.setDepartmentId(employeeService.selectById(s.getEmployeeId()).getDepartmentId());
            s.setTime(salaryManager.getSalaryDate());
            s.setYear(salaryManager.getSalaryDate().substring(0,7));
            s.setNote("全体工资发放");
            salaryMapper.insert(s);
        }
        //计算现金流输出
        Resources resources = resourcesMapper.selectByType(ResourcesEnum.CASH_FLOW.status);
        resources.setPrice(resources.getPrice() - sum);
        resourcesMapper.updateById(resources);
        //财务输出
        Financialout financialout = new Financialout();
        financialout.setPrice(sum);
        financialout.setTime(DateUtil.now());
        financialout.setName(salaryManager.getSalaryDate().substring(0,7) + "全体工资发放");
        Department department = departmentMapper.selectByPowerName(PowerEnum.DEPARTMENT_FINANCIAL.status);
        financialout.setDepartmentId(department.getId());
        financialoutMapper.insert(financialout);


    }

    @Transactional(rollbackFor = Exception.class)
    public void grantOne(Integer id) {
        if(id == null){
            throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        Salary salary = salaryManagerMapper.selectSalaryOne(id);
        if(salary == null){
            throw new CustomException(ResultCodeEnum.SALARY_MANAGER_NO_RECORD);
        }

        if(salary.getPrice() == null){
            throw new CustomException(ResultCodeEnum.SALARY_MANAGER_GETPRICE_RECORD);
        }

        salary.setDepartmentId(employeeService.selectById(salary.getEmployeeId()).getDepartmentId());
        if(salary.getTime() == null){
            salary.setTime(DateUtil.now());
        }
        salary.setYear(salary.getTime().substring(0,7));
        salary.setNote("单独工资发放");
        salaryMapper.insert(salary);

        //资产现金流减少
        Resources resources = resourcesMapper.selectByType(ResourcesEnum.CASH_FLOW.status);
        resources.setPrice(resources.getPrice() - salary.getPrice());
        resourcesMapper.updateById(resources);

        Financialout financialout = new Financialout();
        financialout.setPrice(salary.getPrice());
        financialout.setTime(DateUtil.now());
        financialout.setName(DateUtil.now().substring(0,7) + "单独工资发放");
        Department department = departmentMapper.selectByPowerName(PowerEnum.DEPARTMENT_FINANCIAL.status);
        financialout.setDepartmentId(department.getId());
        financialoutMapper.insert(financialout);

    }

    public double scanner(SalaryManager salaryManager){
        double sum = 0.0;
        if(salaryManager.getBasicSalary() != null){
            sum += salaryManager.getBasicSalary();
        }
        if(salaryManager.getPositionSalary() != null){
            sum += salaryManager.getPositionSalary();
        }
        if(salaryManager.getPerformanceBonus() != null){
            sum += salaryManager.getPerformanceBonus();
        }
        if(salaryManager.getOtherAllowance() != null){
            sum += salaryManager.getOtherAllowance();
        }

        return sum;
    }


    public SalaryManager setSalaryManager(Employee employee) {
        SalaryManager salaryManager = new SalaryManager();
        //该员工有部门,没有部门不做处理

        if(employee.getDepartmentId() != null){
            if(employee.getLevel() == 1){
                salaryManager.setPositionSalary(5000.0);
                salaryManager.setSocialInsurance("单位参保，五险");
                salaryManager.setHousingFund("比例5%");
            }
            if(employee.getLevel() == 2){
                salaryManager.setPositionSalary(10000.0);
                salaryManager.setSocialInsurance("单位参保，七险");
                salaryManager.setHousingFund("比例12%");
            }
        }else{
            salaryManager.setPositionSalary(0.0);
        }
        //基础工资
        if(ObjectUtil.isEmpty(salaryManager.getBasicSalary())){
            salaryManager.setBasicSalary(10000.0);
        }
        if(ObjectUtil.isEmpty(salaryManager.getSalaryDate())){
            String date = this.salaryDate();
            salaryManager.setSalaryDate(date);
        }
        return salaryManager;
    }

    public SalaryManager updateSalaryManager(Employee employee) {
        SalaryManager salaryManager = salaryManagerMapper.selectByEmployeeId(employee.getId());
        if(salaryManager == null){
            throw new CustomException(ResultCodeEnum.SALARY_MANAGER_NO_RECORD);
        }
        if(employee.getDepartmentId() != null){
            if(employee.getLevel() == 1){
                salaryManager.setPositionSalary(5000.0);
                salaryManager.setSocialInsurance("单位参保，五险");
                salaryManager.setHousingFund("比例5%");
            }
            if(employee.getLevel() == 2){
                salaryManager.setPositionSalary(10000.0);
                salaryManager.setSocialInsurance("单位参保，七险");
                salaryManager.setHousingFund("比例12%");
            }
        }else{
            salaryManager.setPositionSalary(0.0);
        }
        return salaryManager;
    }


    /**
     * 发薪日为15号
     * @return
     */
    public  String salaryDate() {
        LocalDate currentDate = LocalDate.now();
        int dayOfMonth = currentDate.getDayOfMonth();

        LocalDate salaryDate;
        if (dayOfMonth <= 15) {
            salaryDate = currentDate.withDayOfMonth(15);
        } else {
            salaryDate = currentDate.plusMonths(1).withDayOfMonth(15);
        }

        LocalDateTime salaryDateTime = salaryDate.atStartOfDay(); // 设置为当天的开始时间，即00:00:00
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return formatter.format(salaryDateTime);
    }


    public void deleteByEmployeeId(Integer id) {
        salaryManagerMapper.deleteByEmployeeId(id);
    }

    public SalaryManager selectById(Integer id) {
        return salaryManagerMapper.selectById(id);
    }
}
