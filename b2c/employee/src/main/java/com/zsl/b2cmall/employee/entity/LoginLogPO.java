package com.zsl.b2cmall.employee.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LoginLogPO implements Serializable {

    private Integer id;
    private Integer userId;
    private String loginIp;
    private String loginDevice;
    private String loginLocation;
    private Date loginTime;
    private String userAgent;
    private Integer status;
}
