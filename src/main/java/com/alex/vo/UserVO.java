package com.alex.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author wsh
 * @date 2020-06-26
 */
@Data
public class UserVO {
    @NotBlank(message = "邮箱不能为空")
    @Email
    private String email;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
    private String repass;
    private String vercode;
}
