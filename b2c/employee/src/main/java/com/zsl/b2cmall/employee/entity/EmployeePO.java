package com.zsl.b2cmall.employee.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class EmployeePO implements Serializable {

    private Integer id;
    private Integer shopId;
    private String username;
    private String password;
    private String avatarUrl;
    private Date lastLoginTime;
    private Integer loginCount;
    private Integer status;
    private Date createdAt;
    private Date updatedAt;
}
