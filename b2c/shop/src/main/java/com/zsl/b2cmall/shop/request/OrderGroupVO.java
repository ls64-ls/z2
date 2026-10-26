package com.zsl.b2cmall.shop.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订单组展示对象：一个订单 + 多个商品行
 */
@Data
public class OrderGroupVO implements Serializable {

    private Integer id;
    private String orderNo;
    private String createdAt;
    private String storeName;
    private String status;
    private String statusLabel;
    private String afterSaleStatus;
    private String afterSaleLabel;
    private String payType;
    private String buyerName;
    private String buyerNick;
    /** 脱敏手机号 */
    private String buyerPhone;
    private String buyerAddress;
    private String logisticsType;
    private String deliveryNo;
    private String orderType;
    private String orderCategory;
    private String productImage;
    private String productName;
    private Integer totalQuantity;
    private java.math.BigDecimal amount;
    private String paidAt;
    private String shippedAt;
    private String completedAt;
    /** 标签集合：待发货/待收货/退款申请中/电子卡券订单/同城配送/赠品 */
    private List<String> tags;
    private List<OrderItemVO> items;
    private String shopName;

    @Data
    public static class OrderItemVO implements Serializable {
        private Integer id;
        private Integer productId;
        private String productName;
        private String productImage;
        private java.math.BigDecimal unitPrice;
        private Integer quantity;
        private Integer isGift;
    }
}
