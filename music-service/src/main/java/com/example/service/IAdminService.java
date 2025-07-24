package com.example.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.example.mode.dto.AdminDTO;
import com.example.mode.entity.Admin;
import com.example.result.Result;


/**
 * <p>
 * 服务类
 * </p>
 */
public interface IAdminService extends IService<Admin> {

    // 管理员注册
    Result register(AdminDTO adminDTO);

    // 管理员登录
    Result login(AdminDTO adminDTO);

    // 退出登录
    Result logout(String token);

    Result info(String token);
}
