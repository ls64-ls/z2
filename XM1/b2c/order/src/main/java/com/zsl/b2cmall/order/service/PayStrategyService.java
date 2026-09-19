package com.zsl.b2cmall.order.service;

import com.zsl.b2cmall.order.enums.PayTypeEnum;
import com.zsl.b2cmall.order.web.request.PayRequestVO;

/**
 * 支付策略接口
 * 不同支付方式实现该接口，由 PayController 根据 payType 路由到对应实现
 */
public interface PayStrategyService {

    /**
     * 执行支付
     *
     * @param payRequestVO 支付请求
     * @return 支付结果描述
     */
    String pay(PayRequestVO payRequestVO);

    /**
     * 获取当前策略对应的支付类型
     *
     * @return 支付类型枚举
     */
    PayTypeEnum payType();
}
