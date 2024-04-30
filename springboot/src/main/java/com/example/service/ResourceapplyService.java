/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/16 23:51
 * @version 1.0
 */

package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.Result;
import com.example.common.enums.*;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.EmployeeMapper;
import com.example.mapper.ResourceapplyMapper;
import com.example.mapper.ResourcesMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ResourceapplyService {

    @Resource
    private ResourceapplyMapper resourceapplyMapper;

    @Resource
    private ResourcesService resourcesService;
    @Resource
    private EmployeeMapper employeeMapper;
    @Resource
    private EmployeeService employeeService;
    @Resource
    private DepartmentService departmentService;
    @Resource
    private ResourcesMapper resourcesMapper;

    /**
     * 新增
     */
    public void add(Resourceapply resourceapply) {
        Integer num = resourceapply.getNum();
        Resources resources = resourcesMapper.selectById(resourceapply.getResourcesId());
        //现金流不可申请
        if(ResourcesEnum.CASH_FLOW.status.equals(resources.getType())){
            throw new CustomException(ResultCodeEnum.CASH_FLOW_NO_APPLY);
        }

        /**
         * 判断资产数量
         */
        if (resources.getNum() < num) {
            throw new CustomException(ResultCodeEnum.RESOURCES_NUM_ERROR);
        }

        Employee employee = employeeMapper.selectById(resourceapply.getEmployeeId());
        resourceapply.setDepartmentId(employee.getDepartmentId());
        resourceapply.setTime(DateUtil.now());

        // 审批进度：主管审核中
        resourceapply.setProcess(ApplyEnum.PROCESS_HEADER_APPLYING.status);
        // 审批状态：待主管审核
        resourceapply.setStatus(ApplyEnum.STATUS_HEADER_APPLYING.status);
        resourceapplyMapper.insert(resourceapply);

        // 该资产数量减少对应的申请数量
        resources.setNum(resources.getNum() - num);
        resourcesMapper.updateById(resources);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        resourceapplyMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            resourceapplyMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Resourceapply resourceapply) {
        //当前提交的状态
        String status = resourceapply.getStatus();
        String process = resourceapply.getProcess();
        Account currentUser = TokenUtils.getCurrentUser();

        //管理员没有审批权限
        if(RoleEnum.ADMIN.name().equals(currentUser.getRole())){
            throw new CustomException(ResultCodeEnum.APPLY_ADMIN_ROLE_ERORR);
        }

        //员工部分
        if(RoleEnum.USER.name().equals(currentUser.getRole())){
            //审批前，判断审批身份与审批的进度相不相符合，符合进行审批，不符合就返回信息
            Employee employee = employeeService.selectById(currentUser.getId());
            Department department = new Department();
            if(employee.getDepartmentId() != null){
                department = departmentService.selectById(employee.getDepartmentId());
            }
            //如果是部长
            if (employee.getLevel() != null && employee.getLevel() == 2) {
                if(!ApplyEnum.PROCESS_HEADER_APPLYING.status.equals(resourceapply.getProcess())){
                    if(PowerEnum.DEPARTMENT_FINANCIAL.status.equals(department.getPowerName())){
                        if (ApplyEnum.APPLY_OK.status.equals(status)) {
                            resourceapply.setStatus(ApplyEnum.APPLY_OK.status);
                        }
                        if (ApplyEnum.APPLY_NO.status.equals(status)) {
                            resourceapply.setStatus(ApplyEnum.STATUS_FINANCIAL_APPLY_NO.status);
                            //结束，资产入库
                            Resources resources = resourcesService.selectById(resourceapply.getResourcesId());
                            resources.setNum(resources.getNum() + resourceapply.getNum());
                            resourcesService.updateById(resources);
                        }

                        //流程到这结束
                        resourceapply.setProcess(ApplyEnum.APPLY_DONE.status);
                        resourceapplyMapper.updateById(resourceapply);
                    }
                    else{
                        throw new CustomException(ResultCodeEnum.APPLY_NO_POWER_ERROR);
                    }
                }
                // 审批通过，要走到财务部那边，设置状态
                if(ApplyEnum.APPLY_OK.status.equals(status)){
                    resourceapply.setStatus(ApplyEnum.STATUS_FINANCIAL_APPLYING.status);
                    resourceapply.setProcess(ApplyEnum.PROCESS_FINANCIAL_APPLYING.status);
                }
                // 审批不通过，直接结束
                if (ApplyEnum.APPLY_NO.status.equals(status)) {
                    resourceapply.setStatus(ApplyEnum.STATUS_HEADER_APPLY_NO.status);
                    resourceapply.setProcess(ApplyEnum.APPLY_DONE.status);
                    //结束，资产入库
                    Resources resources = resourcesService.selectById(resourceapply.getResourcesId());
                    resources.setNum(resources.getNum() + resourceapply.getNum());
                    resourcesService.updateById(resources);
                }

                resourceapplyMapper.updateById(resourceapply);
            }
            //财务部
            else{
                if(!ApplyEnum.PROCESS_FINANCIAL_APPLYING.status.equals(resourceapply.getProcess())){
                    throw new CustomException(ResultCodeEnum.APPLY_NO_POWER_ERROR);
                }
                if (ApplyEnum.APPLY_OK.status.equals(status)) {
                    resourceapply.setStatus(ApplyEnum.APPLY_OK.status);
                }
                if (ApplyEnum.APPLY_NO.status.equals(status)) {
                    resourceapply.setStatus(ApplyEnum.STATUS_FINANCIAL_APPLY_NO.status);
                    //结束，资产入库
                    Resources resources = resourcesService.selectById(resourceapply.getResourcesId());
                    resources.setNum(resources.getNum() + resourceapply.getNum());
                    resourcesService.updateById(resources);
                }
                //流程到这结束
                resourceapply.setProcess(ApplyEnum.APPLY_DONE.status);
                resourceapplyMapper.updateById(resourceapply);
            }
        }
    }

    /**
     * 根据ID查询
     */
    public Resourceapply selectById(Integer id) {
        return resourceapplyMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Resourceapply> selectAll(Resourceapply resourceapply) {
        return resourceapplyMapper.selectAll(resourceapply);
    }

    /**
     * 记录分页查询
     */
    public PageInfo<Resourceapply> selectPage(Resourceapply resourceapply, Integer pageNum, Integer pageSize) {
//        resourceapply.setStatus(ApplyEnum.APPLY_OK.status);

        Account currentUser = TokenUtils.getCurrentUser();
        //如果是员工
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            Employee employee = employeeMapper.selectById(currentUser.getId());
            /**
             * 不做管理员或是员工区分
             */
//            if(employee.getLevel() == 2){
//                resourceapply.setDepartmentId(employee.getDepartmentId());
//            }else{
//                resourceapply.setEmployeeId(currentUser.getId());
//            }
            resourceapply.setEmployeeId(currentUser.getId());
        }
        //管理员不做处理
        PageHelper.startPage(pageNum, pageSize);
        List<Resourceapply> list = resourceapplyMapper.selectAll(resourceapply);
        //查询后处理

        for(Resourceapply resourceapply1 : list){

            if(ApplyEnum.APPLY_DONE.status.equals(resourceapply1.getProcess())){

                if(ApplyEnum.APPLY_OK.status.equals(resourceapply1.getStatus())){
                    resourceapply1.setStatus("审批通过");
                }else{
                    resourceapply1.setStatus("审批不通过");
                }
            }
            else{
                resourceapply1.setStatus("待审批");
            }
        }
        return PageInfo.of(list);
    }

    /**
     * 申请审批分页查询
     */
    public PageInfo<Resourceapply> selectPage2(Resourceapply resourceapply, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        //区分管理员和用户
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            //用户分成3部分

            Employee employee = employeeMapper.selectById(currentUser.getId());
            Employee employee1 = employeeMapper.selectByIdPower(employee);
            // 如果是部长
            if (employee.getLevel() != null && employee.getLevel() == 2 && !PowerEnum.DEPARTMENT_FINANCIAL.status.equals(employee1.getPowerName())) {
                resourceapply.setDepartmentId(employee.getDepartmentId());
                resourceapply.setStatus(ApplyEnum.STATUS_HEADER_APPLYING.status);
            }
            else {
                // 如果是普通员工
                resourceapply.setEmployeeId(employee.getId());
            }

            //如果是财务部
            if(PowerEnum.DEPARTMENT_FINANCIAL.status.equals(employee1.getPowerName())){
                PageHelper.startPage(pageNum, pageSize);
                List<Resourceapply> list = resourceapplyMapper.selectAll(resourceapply);
                return PageInfo.of(list);
            }

        }

        PageHelper.startPage(pageNum, pageSize);
        List<Resourceapply> list = resourceapplyMapper.selectAll(resourceapply);
        return PageInfo.of(list);
    }

    public void updateByAdmin(Resourceapply resourceapply) {
        //判断数量是否发生改变
        Resourceapply resourceapply_o = resourceapplyMapper.selectById(resourceapply.getId());
        Resources resources = resourcesService.selectById(resourceapply.getResourcesId());
        //数量
        if(resourceapply_o.getNum() < resourceapply.getNum()){
            //判断该资产是否有足够的库存
            int change = resourceapply.getNum() - resourceapply_o.getNum();
            if(resources.getNum() > change){
                resources.setNum(resources.getNum() - change);
                resourceapply_o.setNum(resourceapply.getNum());
            }else{
                throw new CustomException(ResultCodeEnum.RESOURCES_NUM_ERROR);
            }
        }
        else if (resourceapply_o.getNum() > resourceapply.getNum()) { //数量减少
            resources.setNum(resources.getNum() + resourceapply_o.getNum() - resourceapply.getNum());
            resourceapply_o.setNum(resourceapply.getNum());
        }
        //审批状态
        if(ApplyEnum.PROCESS_HEADER_APPLYING.status.equals(resourceapply.getProcess())){//部长
            //待审批
            if(ApplyEnum.APPLY_WAIT.status.equals(resourceapply.getStatus())){
                resourceapply_o.setStatus(ApplyEnum.STATUS_HEADER_APPLYING.status);
                resourceapply_o.setProcess(ApplyEnum.PROCESS_HEADER_APPLYING.status);
            }
            //审批通过
            if(ApplyEnum.APPLY_OK.status.equals(resourceapply.getStatus())){
                resourceapply_o.setStatus(ApplyEnum.STATUS_FINANCIAL_APPLYING.status);
                resourceapply_o.setProcess(ApplyEnum.PROCESS_FINANCIAL_APPLYING.status);
            }
            //审批不通过
            if(ApplyEnum.APPLY_NO.status.equals(resourceapply.getStatus())){
                resourceapply_o.setStatus(ApplyEnum.STATUS_HEADER_APPLY_NO.status);
                resourceapply_o.setProcess(ApplyEnum.APPLY_DONE.status);
                //设置资产数量
                resources.setNum(resources.getNum() + resourceapply.getNum());
            }

        } else if (ApplyEnum.PROCESS_FINANCIAL_APPLYING.status.equals(resourceapply.getProcess())) {//财务部
            if(ApplyEnum.APPLY_WAIT.status.equals(resourceapply.getStatus())){
                resourceapply_o.setStatus(ApplyEnum.STATUS_FINANCIAL_APPLYING.status);
                resourceapply_o.setProcess(ApplyEnum.PROCESS_FINANCIAL_APPLYING.status);
            }
            //审批通过
            if(ApplyEnum.APPLY_OK.status.equals(resourceapply.getStatus())){
                resourceapply_o.setStatus(ApplyEnum.STATUS_FINANCIAL_APPLY_OK.status);
                resourceapply_o.setProcess(ApplyEnum.APPLY_DONE.status);
            }
            //审批不通过
            if(ApplyEnum.APPLY_NO.status.equals(resourceapply.getStatus())){
                resourceapply_o.setStatus(ApplyEnum.STATUS_FINANCIAL_APPLY_NO.status);
                resourceapply_o.setProcess(ApplyEnum.APPLY_DONE.status);
                //设置资产数量
                resources.setNum(resources.getNum() + resourceapply.getNum());
            }

        }else if(ApplyEnum.APPLY_DONE.status.equals(resourceapply.getProcess())){//审批结束
            //审批通过
            if(ApplyEnum.APPLY_OK.status.equals(resourceapply.getStatus())){
                resourceapply_o.setStatus(ApplyEnum.STATUS_ADMIN_ALLOW.status);
                resourceapply_o.setProcess(ApplyEnum.APPLY_DONE.status);
            }
            //审批不通过
            if(ApplyEnum.APPLY_NO.status.equals(resourceapply.getStatus())){
                resourceapply_o.setStatus(ApplyEnum.STATUS_ADMIN_STOP.status);
                resourceapply_o.setProcess(ApplyEnum.APPLY_DONE.status);
                //设置资产数量
                resources.setNum(resources.getNum() + resourceapply.getNum());
            }

        }

        resourcesService.updateById(resources);
        resourceapplyMapper.updateById(resourceapply_o);
    }
}