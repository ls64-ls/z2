package com.zsl.b2cmall.shop.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RefundPO implements Serializable {

    private Integer id;
    private Integer orderId;
    private String orderNo;
    private String refundNo;
    private BigDecimal amount;
    private String reason;
    /** 退款申请中/退款完成/已拒绝 */
    private String status;
    private String createdAt;
    private String handledAt;
}
