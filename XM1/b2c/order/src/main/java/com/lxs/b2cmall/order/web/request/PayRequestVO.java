package com.lxs.b2cmall.order.web.request;

import com.lxs.b2cmall.order.enums.PayTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 支付请求参数
 */
@Data
public class PayRequestVO implements Serializable {

    /** 订单ID */
    private String orderId;
    /** 支付金额（单位：分） */
    private Integer shopPrice;
    /** 支付方式 */
    private PayTypeEnum payType;
}
