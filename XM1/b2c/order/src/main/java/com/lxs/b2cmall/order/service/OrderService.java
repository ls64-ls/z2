package com.lxs.b2cmall.order.service;

import com.lxs.b2cmall.order.entity.OrderPO;
import com.lxs.b2cmall.order.web.request.CreateOrderRequestVO;
import com.lxs.b2cmall.order.web.request.PayCallbackRequestVO;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建订单
     */
    OrderPO createOrder(CreateOrderRequestVO req);

    /**
     * 支付回调（待支付 -> 已支付）
     */
    String pay(PayCallbackRequestVO req);

    /**
     * 订单发货（已支付 -> 已发货）
     */
    String sent(Integer orderId);

    /**
     * 确认收货（已发货 -> 已完成）
     */
    String complete(Integer orderId);

    /**
     * 查询订单
     */
    OrderPO getOrder(Integer orderId);
}
