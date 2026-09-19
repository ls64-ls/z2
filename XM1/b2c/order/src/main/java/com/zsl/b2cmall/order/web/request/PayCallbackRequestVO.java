package com.zsl.b2cmall.order.web.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 支付回调请求
 */
@Data
public class PayCallbackRequestVO implements Serializable {

    /** 订单ID */
    private Integer orderId;
    /** 支付流水号 */
    private String tradeNo;
}
