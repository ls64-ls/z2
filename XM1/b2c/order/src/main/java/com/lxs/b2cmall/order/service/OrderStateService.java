package com.lxs.b2cmall.order.service;

import com.lxs.b2cmall.order.bean.OrderContext;
import com.lxs.b2cmall.order.enums.OrderStatus;

/**
 * 订单状态服务接口（状态模式）
 * 每个具体状态实现该接口，定义该状态下可执行的行为
 */
public interface OrderStateService {

    /**
     * 获取当前状态对应的订单状态枚举
     *
     * @return 订单状态
     */
    OrderStatus orderStatus();

    /**
     * 支付操作
     *
     * @param orderContext 订单上下文
     */
    void pay(OrderContext orderContext);

    /**
     * 发货操作
     *
     * @param orderContext 订单上下文
     */
    void sent(OrderContext orderContext);

    /**
     * 完成操作（确认收货）
     *
     * @param orderContext 订单上下文
     */
    void complete(OrderContext orderContext);
}
