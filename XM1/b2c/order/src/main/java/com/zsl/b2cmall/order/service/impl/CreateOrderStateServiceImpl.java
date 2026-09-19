package com.zsl.b2cmall.order.service.impl;

import com.zsl.b2cmall.order.bean.OrderContext;
import com.zsl.b2cmall.order.enums.OrderStatus;
import com.zsl.b2cmall.order.service.OrderStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 已创建状态（待支付）
 * 可执行支付操作，流转为「已支付」状态
 */
@Slf4j
@Component
public class CreateOrderStateServiceImpl implements OrderStateService {

    @Override
    public OrderStatus orderStatus() {
        return OrderStatus.CREATED;
    }

    @Override
    public void pay(OrderContext orderContext) {
        log.info("支付完成");
        orderContext.setCurrentOrderStateService(
                orderContext.getOrderStateServiceMap().get(OrderStatus.PAID));
    }

    @Override
    public void sent(OrderContext orderContext) {
        log.info("订单未支付，无法发货");
    }

    @Override
    public void complete(OrderContext orderContext) {
        log.info("订单未支付，无法完成");
    }
}
