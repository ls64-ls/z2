package com.zsl.b2cmall.order.bean;

import com.google.common.collect.Maps;
import com.zsl.b2cmall.order.entity.OrderPO;
import com.zsl.b2cmall.order.enums.OrderStatus;
import com.zsl.b2cmall.order.service.OrderStateService;

import java.util.List;
import java.util.Map;

/**
 * 订单上下文（状态模式核心）
 * 持有当前订单状态，并将行为委托给当前状态对象处理
 */
public class OrderContext {

    private Integer orderId;
    private OrderStateService currentOrderStateService;
    private Map<OrderStatus, OrderStateService> orderStateServiceMap;

    public OrderContext(OrderPO orderPO, List<OrderStateService> orderStateServiceList) {
        orderStateServiceMap = Maps.newHashMapWithExpectedSize(orderStateServiceList.size());
        orderStateServiceList.forEach(orderStateService ->
                orderStateServiceMap.put(orderStateService.orderStatus(), orderStateService));

        this.orderId = orderPO.getId();
        this.currentOrderStateService = orderStateServiceMap.get(OrderStatus.valueOf(orderPO.getStatus()));
    }

    /**
     * 支付
     */
    public void pay() {
        currentOrderStateService.pay(this);
    }

    /**
     * 发货
     */
    public void sent() {
        currentOrderStateService.sent(this);
    }

    /**
     * 完成（确认收货）
     */
    public void complete() {
        currentOrderStateService.complete(this);
    }

    public Integer getOrderId() {
        return orderId;
    }

    public OrderStateService getCurrentOrderStateService() {
        return currentOrderStateService;
    }

    public void setCurrentOrderStateService(OrderStateService currentOrderStateService) {
        this.currentOrderStateService = currentOrderStateService;
    }

    public Map<OrderStatus, OrderStateService> getOrderStateServiceMap() {
        return orderStateServiceMap;
    }
}
