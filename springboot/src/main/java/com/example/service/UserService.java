/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/15 16:24
 * @version 1.0
 */

package com.example.service;


import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;


    /**获取对象**/
    public User change(Account account){

        User user = new User();
        user.setUsername(account.getUsername());
        user.setPassword(account.getPassword());
        user.setRole(account.getRole());
        user.setRoleid(account.getRole()+"-"+account.getId());
        return user;
    }

    public String getRoleid(Account account){
        return account.getRole()+"-"+account.getId();
    }

    public void add(User user) {
        User dbUser = userMapper.selectByUsername(user.getRoleid());
        if (ObjectUtil.isNotNull(dbUser)) {
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
        }
        if (ObjectUtil.isEmpty(user.getPassword())) {
            user.setPassword(Constants.USER_DEFAULT_PASSWORD);
        }
        userMapper.insert(user);
    }

    /**
     * 删除
     */
    public void deleteByUsername(String username) {
        userMapper.deleteByUsername(username);
    }

    public void deleteByRoleid(String roleid) {  userMapper.deleteByRoleid(roleid);
    }
    /**
     * 修改
     */
    public void updateByRoleid(User user) {
        userMapper.updateByRoleid(user);
    }



    /**
     * 查询所有
     */
    public List<User> selectAll(User user) {
        return userMapper.selectAll(user);
    }


    /**
     * 登录
     */
    public Account login(Account account) {
        Account dbuser = userMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbuser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbuser.getPassword())) {
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
        }
        return dbuser;
    }

    /**
     * 注册
     */
    public void register(Account account) {
        User user = new User();
        BeanUtils.copyProperties(account, user);
        add(user);
    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        User dbuser = userMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbuser)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbuser.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbuser.setPassword(account.getNewPassword());
        userMapper.updateByRoleid(dbuser);
    }



}
