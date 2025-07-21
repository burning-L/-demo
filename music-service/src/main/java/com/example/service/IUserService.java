package com.example.service;



import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mode.dto.*;
import com.example.mode.entity.User;
import com.example.mode.vo.UserManagementVO;
import com.example.result.PageResult;
import com.example.result.Result;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 */
public interface IUserService extends IService<User> {


    // 获取所有用户数量
    Result<Long> getAllUsersCount();

    // 获取所有用户
    Result<PageResult<UserManagementVO>> getAllUsers(UserSearchDTO userSearchDTO);

    // 添加用户
    Result addUser(UserAddDTO userAddDTO);

    // 更新用户
    Result updateUser(UserDTO userDTO);

    // 更新用户状态
    Result updateUserStatus(Long userId, Integer userStatus);

    // 删除用户
    Result deleteUser(Long userId);

    // 批量删除用户
    Result deleteUsers(List<Long> userIds);

    //**********************************************************************//
    // 发送验证码
    Result sendVerificationCode(String email);

    // 验证验证码
    boolean verifyVerificationCode(String email, String verificationCode);

    // 用户注册
    Result register(UserRegisterDTO userRegisterDTO);

    // 重置用户密码
    Result resetUserPassword(UserResetPasswordDTO userResetPasswordDTO);
}
