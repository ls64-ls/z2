package com.zsl.b2cmall.order.service.impl;

import com.zsl.b2cmall.order.enums.PayTypeEnum;
import com.zsl.b2cmall.order.service.PayStrategyService;
import com.zsl.b2cmall.order.web.request.PayRequestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * PayPal 支付策略实现
 */
@Slf4j
@Component
public class PaypalStrategyServiceImpl implements PayStrategyService {

    @Override
    public String pay(PayRequestVO payRequestVO) {
        log.info("【PayPal支付】发起支付，订单号：{}，金额：{} 分", payRequestVO.getOrderId(), payRequestVO.getShopPrice());

        // 模拟调用 PayPal API 创建支付订单

        log.info("【PayPal支付】订单 {} 支付成功", payRequestVO.getOrderId());
        return "PayPal支付成功，订单号：" + payRequestVO.getOrderId();
    }

    @Override
    public PayTypeEnum payType() {
        return PayTypeEnum.PAYPAL;
    }
}
