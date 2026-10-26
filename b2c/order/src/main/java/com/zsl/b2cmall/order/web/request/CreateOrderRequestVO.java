package com.zsl.b2cmall.order.web.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 创建订单请求
 */
@Data
public class CreateOrderRequestVO implements Serializable {

    private Integer shopId;
    private Integer productId;
    private String productName;
    private BigDecimal amount;
    private Integer quantity;
    private String buyerName;
    private String buyerPhone;
    private String buyerAddress;
}
