package com.example.mapper;


import com.example.entity.voucher.VoucherPicture;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import java.util.List;

@Mapper
public interface VoucherPictureMapper {


    void insert(VoucherPicture voucherPicture);


    void updateByReimbursementId(VoucherPicture voucherPicture);

    void deleteByReimbursementId(Integer id);

    List<VoucherPicture> selectByReimbursementId(Integer id);

    void deleteById(Integer id);

    void updateById(VoucherPicture voucherPicture);
}
