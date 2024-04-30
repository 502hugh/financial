package com.example.mapper;


import com.example.entity.voucher.ReimbursementApply;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReimbursementApplyMapper {
    int insert(ReimbursementApply reimbursementApply);

    void updateById(ReimbursementApply reimbursementApply);

    ReimbursementApply selectById(Integer id);

    ReimbursementApply selectByReimbursementId(Integer reimbursementId);
}
