package com.lxs.b2cmall.order.enums;

/**
 * 支付方式枚举
 */
public enum PayTypeEnum {

    ALIPAY(1, "支付宝支付"),
    WECHAT(2, "微信支付"),
    PAYPAL(3, "PayPal支付"),
    UNIONPAY(4, "银联支付");

    private final Integer code;
    private final String desc;

    PayTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static PayTypeEnum fromCode(Integer code) {
        for (PayTypeEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return null;
    }
}
