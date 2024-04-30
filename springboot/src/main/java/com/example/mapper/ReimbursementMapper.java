package com.example.mapper;

import com.example.entity.Resources;
import com.example.entity.voucher.Reimbursement;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReimbursementMapper {


    /**
     * 查询所有
     */
    List<Reimbursement> selectAll(Reimbursement reimbursement);

    /**
     * 删除
     */
    void deleteById(Integer id);

    /**
     * 新增
     */
    void insert(Reimbursement reimbursement);

    /**
     * 修改
     */
    void updateById(Reimbursement reimbursement);
    /**
     * 根据ID查询
     */
    Reimbursement selectById(Integer id);

    Reimbursement selectByName(String name);

    List<Reimbursement> selectApply(Reimbursement reimbursement);
}
