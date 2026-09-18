package com.lxs.b2cmall.order.service.impl;

import com.lxs.b2cmall.order.enums.PayTypeEnum;
import com.lxs.b2cmall.order.service.PayStrategyService;
import com.lxs.b2cmall.order.web.request.PayRequestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 银联支付策略实现
 */
@Slf4j
@Component
public class UnionpayStrategyServiceImpl implements PayStrategyService {

    @Override
    public String pay(PayRequestVO payRequestVO) {
        log.info("【银联支付】发起支付，订单号：{}，金额：{} 分", payRequestVO.getOrderId(), payRequestVO.getShopPrice());

        // 模拟调用银联支付网关

        log.info("【银联支付】订单 {} 支付成功", payRequestVO.getOrderId());
        return "银联支付成功，订单号：" + payRequestVO.getOrderId();
    }

    @Override
    public PayTypeEnum payType() {
        return PayTypeEnum.UNIONPAY;
    }
}
