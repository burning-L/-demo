package com.example.controller;

import com.example.constant.MessageConstant;
import com.example.mode.dto.UserRegisterDTO;
import com.example.mode.dto.UserResetPasswordDTO;
import com.example.result.Result;
import com.example.service.IUserService;
import com.example.util.BindingResultUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;
    /**
     * 发送验证码
     *
     * @param email 邮箱
     * @return 结果
     */
    @GetMapping("/sendVerificationCode")
    public Result sendVerificationCode(@RequestParam @Email String email) {
        return userService.sendVerificationCode(email);
    }

    /**
     * 注册
     *
     * @param userRegisterDTO 用户注册信息
     * @param bindingResult   绑定结果
     * @return 结果
     */
    @PostMapping("/register")
    public Result register(@RequestBody @Valid UserRegisterDTO userRegisterDTO, BindingResult bindingResult) {
        // 校验失败时，返回错误信息
        String errorMessage = BindingResultUtil.handleBindingResultErrors(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }

        // 验证验证码是否正确
        boolean isCodeValid = userService.verifyVerificationCode(userRegisterDTO.getEmail(), userRegisterDTO.getVerificationCode());
        if (!isCodeValid) {
            return Result.error(MessageConstant.VERIFICATION_CODE + MessageConstant.INVALID);
        }

        return userService.register(userRegisterDTO);
    }

    /**
     * 重置用户密码
     *
     * @param userResetPasswordDTO 用户密码信息
     * @return 结果
     */
    @PatchMapping("/resetUserPassword")
    public Result resetUserPassword(@RequestBody @Valid UserResetPasswordDTO userResetPasswordDTO, BindingResult bindingResult) {
        // 校验失败时，返回错误信息
        String errorMessage = BindingResultUtil.handleBindingResultErrors(bindingResult);
        if (errorMessage != null) {
            return Result.error(errorMessage);
        }

        // 验证验证码是否正确
        boolean isCodeValid = userService.verifyVerificationCode(userResetPasswordDTO.getEmail(), userResetPasswordDTO.getVerificationCode());
        if (!isCodeValid) {
            return Result.error(MessageConstant.VERIFICATION_CODE + MessageConstant.INVALID);
        }

        return userService.resetUserPassword(userResetPasswordDTO);
    }
}
