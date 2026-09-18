package com.lxs.b2cmall.employee.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class LoginRequestVO implements Serializable {

    @NotNull(message = "店铺ID不能为空")
    private Integer shopId;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
