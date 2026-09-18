package com.lxs.b2cmall.order.service.impl;

import com.lxs.b2cmall.order.bean.OrderContext;
import com.lxs.b2cmall.order.enums.OrderStatus;
import com.lxs.b2cmall.order.service.OrderStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 已取消状态（终态）
 */
@Slf4j
@Component
public class CancelledStateServiceImpl implements OrderStateService {

    @Override
    public OrderStatus orderStatus() {
        return OrderStatus.CANCELLED;
    }

    @Override
    public void pay(OrderContext orderContext) {
        log.info("订单已取消，无法支付");
    }

    @Override
    public void sent(OrderContext orderContext) {
        log.info("订单已取消，无法发货");
    }

    @Override
    public void complete(OrderContext orderContext) {
        log.info("订单已取消，无法完成");
    }
}
