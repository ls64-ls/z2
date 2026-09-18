package com.lxs.b2cmall.order.service.impl;

import com.lxs.b2cmall.order.bean.OrderContext;
import com.lxs.b2cmall.order.dao.OrderMapper;
import com.lxs.b2cmall.order.entity.OrderPO;
import com.lxs.b2cmall.order.enums.OrderStatus;
import com.lxs.b2cmall.order.service.OrderService;
import com.lxs.b2cmall.order.service.OrderStateService;
import com.lxs.b2cmall.order.web.request.CreateOrderRequestVO;
import com.lxs.b2cmall.order.web.request.PayCallbackRequestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 订单服务实现
 * 使用状态模式流转订单状态
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private List<OrderStateService> orderStateServices;

    @Autowired
    private ApplicationEventPublisher eventBus;

    @Override
    public OrderPO createOrder(CreateOrderRequestVO req) {
        OrderPO orderPO = new OrderPO();
        BeanUtils.copyProperties(req, orderPO);
        orderPO.setStatus(OrderStatus.CREATED.name());
        orderMapper.save(orderPO);

        // 发布订单创建事件
        try {
            eventBus.publishEvent("order:create:" + orderPO.getId());
        } catch (Exception e) {
            log.warn("发布订单创建事件失败: {}", e.getMessage());
        }

        log.info("订单创建成功，订单号：{}", orderPO.getId());
        return orderPO;
    }

    @Override
    public String pay(PayCallbackRequestVO req) {
        OrderPO orderPO = orderMapper.findById(req.getOrderId());
        if (orderPO == null) {
            return "订单不存在";
        }
        String beforeStatus = orderPO.getStatus();
        OrderContext context = new OrderContext(orderPO, orderStateServices);
        context.pay();
        String afterStatus = context.getCurrentOrderStateService().orderStatus().name();
        orderMapper.updateStatus(orderPO.getId(), afterStatus);
        if (beforeStatus.equals(afterStatus)) {
            return "当前订单状态【" + beforeStatus + "】不允许支付操作";
        }
        return "支付成功，订单状态：" + afterStatus;
    }

    @Override
    public String sent(Integer orderId) {
        OrderPO orderPO = orderMapper.findById(orderId);
        if (orderPO == null) {
            return "订单不存在";
        }
        String beforeStatus = orderPO.getStatus();
        OrderContext context = new OrderContext(orderPO, orderStateServices);
        context.sent();
        String afterStatus = context.getCurrentOrderStateService().orderStatus().name();
        orderMapper.updateStatus(orderPO.getId(), afterStatus);
        if (beforeStatus.equals(afterStatus)) {
            return "当前订单状态【" + beforeStatus + "】不允许发货操作";
        }
        return "发货成功，订单状态：" + afterStatus;
    }

    @Override
    public String complete(Integer orderId) {
        OrderPO orderPO = orderMapper.findById(orderId);
        if (orderPO == null) {
            return "订单不存在";
        }
        String beforeStatus = orderPO.getStatus();
        OrderContext context = new OrderContext(orderPO, orderStateServices);
        context.complete();
        String afterStatus = context.getCurrentOrderStateService().orderStatus().name();
        orderMapper.updateStatus(orderPO.getId(), afterStatus);
        if (beforeStatus.equals(afterStatus)) {
            return "当前订单状态【" + beforeStatus + "】不允许确认收货操作";
        }
        return "确认收货成功，订单状态：" + afterStatus;
    }

    @Override
    public OrderPO getOrder(Integer orderId) {
        return orderMapper.findById(orderId);
    }
}
