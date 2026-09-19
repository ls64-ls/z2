package com.zsl.b2cmall.order.service.impl;

import com.zsl.b2cmall.order.enums.PayTypeEnum;
import com.zsl.b2cmall.order.service.PayStrategyService;
import com.zsl.b2cmall.order.web.request.PayRequestVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 支付宝支付策略实现
 */
@Slf4j
@Component
public class AlipayStrategyServiceImpl implements PayStrategyService {

    @Override
    public String pay(PayRequestVO payRequestVO) {
        log.info("【支付宝支付】发起支付，订单号：{}，金额：{} 分", payRequestVO.getOrderId(), payRequestVO.getShopPrice());

        // 模拟调用支付宝 SDK 下单
        // 实际场景：构建 AlipayTradeAppPayRequest，调用 alipayClient.pageExecute(request)
        // AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, format, charset, alipayPublicKey, signType);
        // AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        // AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        // model.setBody("订单支付");
        // model.setSubject("订单支付");
        // model.setOutTradeNo(payRequestVO.getOrderId());
        // model.setTimeoutExpress("30m");
        // model.setTotalAmount(String.valueOf(payRequestVO.getShopPrice() / 100.0));
        // model.setProductCode("QUICK_MSECURITY_PAY");
        // request.setBizModel(model);
        // request.setNotifyUrl("http://127.0.0.1:8083/order/pay/alipay/callback");
        // AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);

        log.info("【支付宝支付】订单 {} 支付成功", payRequestVO.getOrderId());
        return "支付宝支付成功，订单号：" + payRequestVO.getOrderId();
    }

    @Override
    public PayTypeEnum payType() {
        return PayTypeEnum.ALIPAY;
    }
}
