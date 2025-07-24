package com.example.mode.vo;

import lombok.Data;

import java.io.Serial;
import java.util.List;
@Data
public class AdminInfoVO {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 用户名
     */
    private String name;
    /**
     * 用户头像URL
     */
    private String avatar;
    /**
     * 用户角色列表
     */
    private List<String> roles;
    /**
     * 用户按钮权限列表
     */
    private List<String> buttons;
    /**
     * 用户可访问的路由名称列表
     */
    private List<String> routes;
}
