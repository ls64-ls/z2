package com.zsl.b2cmall.shop.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Shop implements Serializable {

    private Integer id;
    private String shopName;
    private String adminAccount;
    private String adminPassword;
    private String logoUrl;
    private Integer status;
    private Date createdAt;
    private Date updatedAt;
}
