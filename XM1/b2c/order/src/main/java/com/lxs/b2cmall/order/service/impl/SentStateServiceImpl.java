package com.lxs.b2cmall.order.service.impl;

import com.lxs.b2cmall.order.bean.OrderContext;
import com.lxs.b2cmall.order.enums.OrderStatus;
import com.lxs.b2cmall.order.service.OrderStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 已发货状态
 * 可执行确认收货操作，流转为「已完成」状态
 */
@Slf4j
@Component
public class SentStateServiceImpl implements OrderStateService {

    @Override
    public OrderStatus orderStatus() {
        return OrderStatus.SENT;
    }

    @Override
    public void pay(OrderContext orderContext) {
        log.info("订单已支付");
    }

    @Override
    public void sent(OrderContext orderContext) {
        log.info("订单已发货，无需重复发货");
    }

    @Override
    public void complete(OrderContext orderContext) {
        log.info("确认收货完成");
        orderContext.setCurrentOrderStateService(
                orderContext.getOrderStateServiceMap().get(OrderStatus.COMPLETED));
    }
}
