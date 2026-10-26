package com.zsl.b2cmall.order.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单实体
 */
@Data
public class OrderPO implements Serializable {

    private Integer id;
    private Integer shopId;
    private Integer productId;
    private String productName;
    private BigDecimal amount;
    private Integer quantity;
    /** 订单状态：CREATED/PAID/SENT/COMPLETED/CANCELLED */
    private String status;
    private String buyerName;
    private String buyerPhone;
    private String buyerAddress;
    private String createdAt;
}
