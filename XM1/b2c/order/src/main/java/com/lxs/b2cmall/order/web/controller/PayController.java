package com.lxs.b2cmall.order.web.controller;

import com.google.common.collect.Maps;
import com.lxs.b2cmall.common.response.BaseResponseVO;
import com.lxs.b2cmall.order.entity.OrderPO;
import com.lxs.b2cmall.order.enums.PayTypeEnum;
import com.lxs.b2cmall.order.service.OrderService;
import com.lxs.b2cmall.order.service.PayStrategyService;
import com.lxs.b2cmall.order.web.request.CreateOrderRequestVO;
import com.lxs.b2cmall.order.web.request.PayCallbackRequestVO;
import com.lxs.b2cmall.order.web.request.PayRequestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 * 支付使用策略模式，订单状态流转使用状态模式
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class PayController {

    @Autowired
    private List<PayStrategyService> payStrategyServices;

    private Map<PayTypeEnum, PayStrategyService> payStrategyServiceMap;

    @Autowired
    private OrderService orderService;

    @PostConstruct
    public void init() {
        payStrategyServiceMap = Maps.newHashMapWithExpectedSize(payStrategyServices.size());
        payStrategyServices.forEach(payStrategyService ->
                payStrategyServiceMap.put(payStrategyService.payType(), payStrategyService));
        log.info("支付策略初始化完成，共 {} 种支付方式", payStrategyServiceMap.size());
    }

    /**
     * 创建订单
     */
    @PostMapping("/create")
    public BaseResponseVO<OrderPO> create(@RequestBody CreateOrderRequestVO req) {
        OrderPO order = orderService.createOrder(req);
        return BaseResponseVO.success(order);
    }

    /**
     * 下单支付（策略模式 + 状态模式）
     */
    @PostMapping("/pay/pay")
    public BaseResponseVO<String> pay(@RequestBody PayRequestVO req) {
        if (req.getPayType() == null) {
            return BaseResponseVO.fail("支付方式不能为空");
        }
        PayStrategyService strategy = payStrategyServiceMap.get(req.getPayType());
        if (strategy == null) {
            return BaseResponseVO.fail("不支持的支付方式：" + req.getPayType());
        }

        // 1. 策略模式：执行支付
        String payResult = strategy.pay(req);

        // 2. 状态模式：支付成功后流转订单状态（CREATED -> PAID）
        PayCallbackRequestVO callback = new PayCallbackRequestVO();
        callback.setOrderId(Integer.valueOf(req.getOrderId()));
        String stateResult = orderService.pay(callback);

        return BaseResponseVO.success(payResult + "，" + stateResult);
    }

    /**
     * 查询订单状态
     */
    @GetMapping("/status/{orderId}")
    public BaseResponseVO<OrderPO> getOrderStatus(@PathVariable("orderId") Integer orderId) {
        OrderPO order = orderService.getOrder(orderId);
        if (order == null) {
            return BaseResponseVO.fail("订单不存在");
        }
        return BaseResponseVO.success(order);
    }

    /**
     * 订单发货（PAID -> SENT）
     */
    @PostMapping("/sent/{orderId}")
    public BaseResponseVO<String> sent(@PathVariable("orderId") Integer orderId) {
        return BaseResponseVO.success(orderService.sent(orderId));
    }

    /**
     * 确认收货（SENT -> COMPLETED）
     */
    @PostMapping("/complete/{orderId}")
    public BaseResponseVO<String> complete(@PathVariable("orderId") Integer orderId) {
        return BaseResponseVO.success(orderService.complete(orderId));
    }
}
