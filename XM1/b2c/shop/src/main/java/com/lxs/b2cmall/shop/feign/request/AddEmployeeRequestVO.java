package com.lxs.b2cmall.shop.feign.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddEmployeeRequestVO implements Serializable {

    private Integer shopId;
    private String username;
    private String password;
}
