package com.lxs.b2cmall.shop.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Message implements Serializable {

    private Integer id;
    private Integer shopId;
    private Integer senderId;
    private String title;
    private String content;
    private Integer msgType;
    private Boolean isRead;
    private Date readTime;
    private Date createdAt;
    private Date updatedAt;
}
