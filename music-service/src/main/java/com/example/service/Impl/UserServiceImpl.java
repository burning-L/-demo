package com.example.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.constant.MessageConstant;
import com.example.enumeration.UserStatusEnum;
import com.example.mapper.UserMapper;
import com.example.mode.dto.*;
import com.example.mode.entity.User;
import com.example.mode.vo.UserManagementVO;
import com.example.result.PageResult;
import com.example.result.Result;
import com.example.service.EmailService;
import com.example.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@CacheConfig(cacheNames = "userCache")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private EmailService emailService;

    /**
     * 添加用户
     *
     * @param userAddDTO 用户信息
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "userCache", allEntries = true)
    public Result addUser(UserAddDTO userAddDTO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userAddDTO.getUsername())
                .or()
                .eq("phone", userAddDTO.getPhone())
                .or()
                .eq("email", userAddDTO.getEmail());
        List<User> existingUsers = userMapper.selectList(queryWrapper);
        if (existingUsers != null && !existingUsers.isEmpty()) {
            for (User user : existingUsers) {
                if (user.getUsername().equals(userAddDTO.getUsername())) {
                    return Result.error(MessageConstant.USERNAME + MessageConstant.ALREADY_EXISTS);
                }
                if (user.getPhone().equals(userAddDTO.getPhone())) {
                    return Result.error(MessageConstant.PHONE + MessageConstant.ALREADY_EXISTS);
                }
                if (user.getEmail().equals(userAddDTO.getEmail())) {
                    return Result.error(MessageConstant.EMAIL + MessageConstant.ALREADY_EXISTS);
                }
            }
        }
        String passwordMD5 = DigestUtils.md5DigestAsHex(userAddDTO.getPassword().getBytes());
        User user = new User();
        user.setUsername(userAddDTO.getUsername()).setPassword(passwordMD5).setPhone(userAddDTO.getPhone())
                .setEmail(userAddDTO.getEmail()).setIntroduction(userAddDTO.getIntroduction())
                .setCreateTime(LocalDateTime.now()).setUpdateTime(LocalDateTime.now());
        // 前端传递的用户状态（1：启用，0：禁用）需反转
        if (userAddDTO.getUserStatus().getId() == 1) {
            user.setUserStatus(UserStatusEnum.ENABLE);  // 数据库（0：启用）
        } else if (userAddDTO.getUserStatus().getId() == 0) {
            user.setUserStatus(UserStatusEnum.DISABLE);    // 数据库（1：禁用）
        }
        if (userMapper.insert(user) == 0) {
            return Result.error(MessageConstant.ADD + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.ADD + MessageConstant.SUCCESS);
    }
    /**
     * 获取所有用户数量
     *
     * @return 用户数量
     */
    @Override
    public Result<Long> getAllUsersCount() {
        return Result.success(userMapper.selectCount(new QueryWrapper<>()));
    }
    /**
     * 分页查询所有用户
     *
     * @param userSearchDTO 用户查询条件
     * @return 用户分页信息
     */
    @Override
    @Cacheable(key = "#userSearchDTO.pageNum + '-' + #userSearchDTO.pageSize + '-' + #userSearchDTO.username + '-' + #userSearchDTO.phone + '-' + #userSearchDTO.userStatus")
    public Result<PageResult<UserManagementVO>> getAllUsers(UserSearchDTO userSearchDTO) {
        System.out.println("getPageCount = " + userSearchDTO.getPageNum() + ", " + userSearchDTO.getPageSize());
        // 分页查询
        Page<User> page = new Page<>(userSearchDTO.getPageNum(), userSearchDTO.getPageSize());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 根据 userSearchDTO 的条件构建查询条件
        if (userSearchDTO.getUsername() != null) {
            queryWrapper.like("username", userSearchDTO.getUsername());
        }
        if (userSearchDTO.getPhone() != null) {
            queryWrapper.like("phone", userSearchDTO.getPhone());
        }
        if (userSearchDTO.getUserStatus() != null) {
            queryWrapper.eq("status", userSearchDTO.getUserStatus().getId());
        }
        // 倒序排序
        queryWrapper.orderByDesc("create_time");
        IPage<User> userPage = userMapper.selectPage(page, queryWrapper);
        System.out.println("userPage = " + userPage);
        //当前页的数据列表是否为0
        if (userPage.getRecords().size() == 0) {
            return Result.success(MessageConstant.DATA_NOT_FOUND, new PageResult<>(0L, null));
        }
        // 转换为 UserManagementVO
        List<UserManagementVO> userVOList = userPage.getRecords().stream()
                .map(user -> {
                    UserManagementVO userVO = new UserManagementVO();
                    BeanUtils.copyProperties(user, userVO);
                    return userVO;
                }).toList();
        return Result.success(new PageResult<>(userPage.getTotal(), userVOList));
    }

    @Override
    @CacheEvict(cacheNames = "userCache", allEntries = true)
    public Result updateUser(UserDTO userDTO) {
        Long userId = userDTO.getUserId();
        User userByUsername = userMapper.selectOne(new QueryWrapper<User>().eq("username", userDTO.getUsername()));
        if (userByUsername != null && !userByUsername.getUserId().equals(userId)) {
            return Result.error(MessageConstant.USERNAME + MessageConstant.ALREADY_EXISTS);
        }

        User userByPhone = userMapper.selectOne(new QueryWrapper<User>().eq("phone", userDTO.getPhone()));
        if (userByPhone != null && !userByPhone.getUserId().equals(userId)) {
            return Result.error(MessageConstant.PHONE + MessageConstant.ALREADY_EXISTS);
        }

        User userByEmail = userMapper.selectOne(new QueryWrapper<User>().eq("email", userDTO.getEmail()));
        if (userByEmail != null && !userByEmail.getUserId().equals(userId)) {
            return Result.error(MessageConstant.EMAIL + MessageConstant.ALREADY_EXISTS);
        }
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);  //将userDTO复制到user
        user.setUpdateTime(LocalDateTime.now());

        if (userMapper.updateById(user) == 0) {
            return Result.error(MessageConstant.UPDATE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS);
    }

    /**
     * 更新用户状态
     *
     * @param userId     用户id
     * @param userStatus 状态
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "userCache", allEntries = true)
    public Result updateUserStatus(Long userId, Integer userStatus) {
        // 确保用户状态有效
        UserStatusEnum statusEnum;
        if (userStatus == 0) {
            statusEnum = UserStatusEnum.ENABLE;
        } else if (userStatus == 1) {
            statusEnum = UserStatusEnum.DISABLE;
        } else {
            return Result.error(MessageConstant.USER_STATUS_INVALID);
        }

        // 更新用户状态
        User user = new User();
        user.setUserStatus(statusEnum).setUpdateTime(LocalDateTime.now());

        int rows = userMapper.update(user, new QueryWrapper<User>().eq("id", userId));
        if (rows == 0) {
            return Result.error(MessageConstant.UPDATE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS);
    }
    /**
     * 删除用户
     *
     * @param userId 用户id
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "userCache", allEntries = true)
    public Result deleteUser(Long userId) {
        if (userMapper.deleteById(userId) == 0) {
            return Result.error(MessageConstant.DELETE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS);
    }

    /**
     * 批量删除用户
     *
     * @param userIds 用户id数组
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "userCache", allEntries = true)
    public Result deleteUsers(List<Long> userIds) {
        if (userMapper.deleteByIds(userIds) == 0) {
            return Result.error(MessageConstant.DELETE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS);
    }

    //**********************************************************************//
    /**
     * 发送验证码
     *
     * @param email 用户邮箱
     * @return 结果
     */
    @Override
    public Result sendVerificationCode(String email) {
        String verificationCode = emailService.sendVerificationCodeEmail(email);
        if (verificationCode == null) {
            return Result.error(MessageConstant.EMAIL_SEND_FAILED);
        }

        // 将验证码存储到Redis中，设置过期时间为5分钟
        stringRedisTemplate.opsForValue().set("verificationCode:" + email, verificationCode, 5, TimeUnit.MINUTES);
        return Result.success(MessageConstant.EMAIL_SEND_SUCCESS);
    }
    /**
     * 验证验证码
     *
     * @param email            用户邮箱
     * @param verificationCode 验证码
     * @return 验证结果
     */
    @Override
    public boolean verifyVerificationCode(String email, String verificationCode) {
        String storedCode = stringRedisTemplate.opsForValue().get("verificationCode:" + email);
        return storedCode != null && storedCode.equals(verificationCode);
    }

    /**
     * 用户注册
     *
     * @param userRegisterDTO 用户注册信息
     * @return 结果
     */
    @Override
    @CacheEvict(cacheNames = "userCache", allEntries = true)
    public Result register(UserRegisterDTO userRegisterDTO) {
        // 删除Redis中的验证码
        stringRedisTemplate.delete("verificationCode:" + userRegisterDTO.getEmail());

        User userByUsername = userMapper.selectOne(new QueryWrapper<User>().eq("username", userRegisterDTO.getUsername()));
        if (userByUsername != null) {
            return Result.error(MessageConstant.USERNAME + MessageConstant.ALREADY_EXISTS);
        }

        User userByEmail = userMapper.selectOne(new QueryWrapper<User>().eq("email", userRegisterDTO.getEmail()));
        if (userByEmail != null) {
            return Result.error(MessageConstant.EMAIL + MessageConstant.ALREADY_EXISTS);
        }

        String passwordMD5 = DigestUtils.md5DigestAsHex(userRegisterDTO.getPassword().getBytes());
        User user = new User();
        user.setUsername(userRegisterDTO.getUsername()).setPassword(passwordMD5).setEmail(userRegisterDTO.getEmail())
                .setCreateTime(LocalDateTime.now()).setUpdateTime(LocalDateTime.now())
                .setUserStatus(UserStatusEnum.ENABLE);

        if (userMapper.insert(user) == 0) {
            return Result.error(MessageConstant.REGISTER + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.REGISTER + MessageConstant.SUCCESS);
    }

    /**
     * 重置用户密码
     *
     * @param userResetPasswordDTO 用户密码信息
     * @return 结果
     */
    @Override
    public Result resetUserPassword(UserResetPasswordDTO userResetPasswordDTO) {
        // 删除Redis中的验证码
        stringRedisTemplate.delete("verificationCode:" + userResetPasswordDTO.getEmail());

        User user = userMapper.selectOne(new QueryWrapper<User>().eq("email", userResetPasswordDTO.getEmail()));
        if (user == null) {
            return Result.error(MessageConstant.EMAIL + MessageConstant.NOT_EXIST);
        }

        if (!userResetPasswordDTO.getRepeatPassword().equals(userResetPasswordDTO.getNewPassword())) {
            return Result.error(MessageConstant.PASSWORD_NOT_MATCH);
        }

        if (userMapper.update(new User().setPassword(DigestUtils.md5DigestAsHex(userResetPasswordDTO.getNewPassword().getBytes())).setUpdateTime(LocalDateTime.now()),
                new QueryWrapper<User>().eq("id", user.getUserId())) == 0) {
            return Result.error(MessageConstant.PASSWORD + MessageConstant.RESET + MessageConstant.FAILED);
        }

        return Result.success(MessageConstant.PASSWORD + MessageConstant.RESET + MessageConstant.SUCCESS);
    }
}
