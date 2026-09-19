package com.zsl.b2cmall.order.service.impl;

import com.zsl.b2cmall.order.bean.OrderContext;
import com.zsl.b2cmall.order.enums.OrderStatus;
import com.zsl.b2cmall.order.service.OrderStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 已完成状态（终态）
 */
@Slf4j
@Component
public class CompletedStateServiceImpl implements OrderStateService {

    @Override
    public OrderStatus orderStatus() {
        return OrderStatus.COMPLETED;
    }

    @Override
    public void pay(OrderContext orderContext) {
        log.info("订单已完成，无需支付");
    }

    @Override
    public void sent(OrderContext orderContext) {
        log.info("订单已完成，无需发货");
    }

    @Override
    public void complete(OrderContext orderContext) {
        log.info("订单已完成");
    }
}
