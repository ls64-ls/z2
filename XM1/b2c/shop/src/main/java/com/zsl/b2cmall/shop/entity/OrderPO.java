package com.zsl.b2cmall.shop.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderPO implements Serializable {

    private Integer id;
    private Integer shopId;
    private Integer productId;
    private String productName;
    private BigDecimal amount;
    private Integer quantity;
    /** 0=待支付, 1=已支付, 2=已发货, 3=已完成, 4=已取消 */
    private Integer status;
    private String buyerName;
    private String buyerPhone;
    private String buyerAddress;
    private String createdAt;
}
