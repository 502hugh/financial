/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/28 16:19
 * @version 1.0
 */

package com.example.service;

import com.example.entity.voucher.Reimbursement;
import com.example.entity.voucher.VoucherPicture;
import com.example.mapper.VoucherPictureMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class VoucherPictureService {


    @Resource
    private VoucherPictureMapper voucherPictureMapper;




    public List<VoucherPicture> selectByReimbursementId(Integer id) {
        List<VoucherPicture> voucherPicture = voucherPictureMapper.selectByReimbursementId(id);
        return voucherPicture;
    }

    public void deleteById(Integer id) {
        voucherPictureMapper.deleteById(id);
    }


    public void add(VoucherPicture voucherPicture) {
        voucherPictureMapper.insert(voucherPicture);
    }

    public void updateById(VoucherPicture voucherPicture) {
        voucherPictureMapper.updateById(voucherPicture);
    }
}
