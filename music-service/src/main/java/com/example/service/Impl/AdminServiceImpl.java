package com.example.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.JwtClaimsConstant;
import com.example.constant.MessageConstant;
import com.example.enumeration.RoleEnum;
import com.example.mapper.AdminMapper;
import com.example.mode.dto.AdminDTO;
import com.example.mode.entity.Admin;
import com.example.mode.vo.AdminInfoVO;
import com.example.result.Result;
import com.example.service.IAdminService;
import com.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 管理员注册
     *
     * @param adminDTO 管理员信息
     * @return 结果
     */
    @Override
    public Result register(AdminDTO adminDTO) {
        Admin admin = adminMapper.selectOne(new QueryWrapper<Admin>().eq("username", adminDTO.getUsername()));
        if (admin != null) {
            return Result.error(MessageConstant.USERNAME + MessageConstant.ALREADY_EXISTS);
        }

        String passwordMD5 = DigestUtils.md5DigestAsHex(adminDTO.getPassword().getBytes());
        Admin adminRegister = new Admin();
        adminRegister.setUsername(adminDTO.getUsername()).setPassword(passwordMD5);

        if (adminMapper.insert(adminRegister) == 0) {
            return Result.error(MessageConstant.REGISTER + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.REGISTER + MessageConstant.SUCCESS);
    }

    @Override
    public Result login(AdminDTO adminDTO) {
        Admin admin = adminMapper.selectOne(new QueryWrapper<Admin>().eq("username", adminDTO.getUsername()));
        if (admin == null) {
            return Result.error(MessageConstant.USERNAME + MessageConstant.ERROR);
        }

        if (DigestUtils.md5DigestAsHex(adminDTO.getPassword().getBytes()).equals(admin.getPassword())) {
            // 登录成功
            Map<String, Object> claims = new HashMap<>();
            claims.put(JwtClaimsConstant.ROLE, RoleEnum.ADMIN.getRole());
            claims.put(JwtClaimsConstant.ADMIN_ID, admin.getAdminId());
            claims.put(JwtClaimsConstant.USERNAME, admin.getUsername());
            String token = JwtUtil.generateToken(claims);

            // 将token存入redis
            stringRedisTemplate.opsForValue().set(token, token, 6, TimeUnit.HOURS);

            return Result.success(MessageConstant.LOGIN + MessageConstant.SUCCESS, token);
        }

        return Result.error(MessageConstant.PASSWORD + MessageConstant.ERROR);
    }

    /**
     * 登出
     *
     * @param token 认证token
     * @return 结果
     */
    @Override
    public Result logout(String token) {
        // 注销token
        Boolean result = stringRedisTemplate.delete(token);
        if (result != null && result) {
            return Result.success(MessageConstant.LOGOUT + MessageConstant.SUCCESS);
        } else {
            return Result.error(MessageConstant.LOGOUT + MessageConstant.FAILED);
        }
    }

    @Override
    public Result info(String token) {
        if (stringRedisTemplate.hasKey(token)) {
            Map<String, Object> claims = JwtUtil.parseToken(token);
            String username = (String) claims.get(JwtClaimsConstant.USERNAME);
            String role = (String) claims.get(JwtClaimsConstant.ROLE);
            AdminInfoVO  adminInfoVO = new AdminInfoVO();
            adminInfoVO.setName(username);
            List<String> roles = new ArrayList<>();
            roles.add(role);
            adminInfoVO.setRoles(roles);
            List<String> routes = Arrays.asList(
                    "home", "Acl", "User", "Role", "Permission",
                    "Product", "Trademark", "Attr", "Song", "SongSku"
            );
            List<String> buttons = new ArrayList<>();
            buttons.add("btn.Trademark.add");
            adminInfoVO.setButtons(buttons);
            adminInfoVO.setRoutes(routes);
            adminInfoVO.setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
            return Result.success("获取用户信息成功",adminInfoVO);
        }
        return Result.error("获取用户信息失败，token无效或已过期");
    }
}
