package com.zsl.b2cmall.order.service.impl;

import com.zsl.b2cmall.order.bean.OrderContext;
import com.zsl.b2cmall.order.enums.OrderStatus;
import com.zsl.b2cmall.order.service.OrderStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 已支付状态
 * 可执行发货操作，流转为「已发货」状态
 */
@Slf4j
@Component
public class PayStateServiceImpl implements OrderStateService {

    @Override
    public OrderStatus orderStatus() {
        return OrderStatus.PAID;
    }

    @Override
    public void pay(OrderContext orderContext) {
        log.info("订单已支付，无需重复支付");
    }

    @Override
    public void sent(OrderContext orderContext) {
        log.info("发货完成");
        orderContext.setCurrentOrderStateService(
                orderContext.getOrderStateServiceMap().get(OrderStatus.SENT));
    }

    @Override
    public void complete(OrderContext orderContext) {
        log.info("订单未发货，无法完成");
    }
}
