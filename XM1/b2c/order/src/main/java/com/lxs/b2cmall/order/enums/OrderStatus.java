package com.lxs.b2cmall.order.enums;

/**
 * 订单状态枚举
 */
public enum OrderStatus {

    /** 已创建（待支付） */
    CREATED,
    /** 已支付 */
    PAID,
    /** 已发货 */
    SENT,
    /** 已完成 */
    COMPLETED,
    /** 已取消 */
    CANCELLED
}
