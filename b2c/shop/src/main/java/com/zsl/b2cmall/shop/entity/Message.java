package com.zsl.b2cmall.shop.entity;

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
    /** 1=订单支付成功 2=退款申请 3=确认收货 */
    private Integer msgType;
    private Boolean isRead;
    private Integer orderId;
    private String orderNo;
    private Date readTime;
    private Date createdAt;
    private Date updatedAt;
}
