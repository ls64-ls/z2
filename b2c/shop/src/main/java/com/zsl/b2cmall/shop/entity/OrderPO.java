package com.zsl.b2cmall.shop.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderPO implements Serializable {

    private Integer id;
    private Integer shopId;
    private Integer productId;
    private String productName;
    /** 订单实付总金额 */
    private BigDecimal amount;
    private Integer quantity;
    /** 订单状态：CREATED/PAID/SENT/COMPLETED/CANCELLED/GROUPING */
    private String status;
    private String buyerName;
    private String buyerPhone;
    private String buyerAddress;
    private String createdAt;

    private String orderNo;
    /** 订单类型：普通订单/秒杀订单/拼团订单 */
    private String orderType;
    /** 订单类别：普通订单/电子卡券订单 */
    private String orderCategory;
    private String payType;
    /** 售后状态：暂无售后/退款申请中/退款完成 */
    private String afterSaleStatus;
    /** 物流类型：普通快递/同城配送 */
    private String logisticsType;
    private String storeName;
    private String deliveryNo;
    private Integer userId;
    private String buyerNick;
    private String productImage;
    private String paidAt;
    private String shippedAt;
    private String completedAt;
    private Integer deleted;

    /** 状态中文名 */
    public String getStatusLabel() {
        return statusLabel(status);
    }

    /** 售后状态中文名 */
    public String getAfterSaleLabel() {
        return afterSaleStatus == null || afterSaleStatus.isEmpty() ? "暂无售后" : afterSaleStatus;
    }

    public static String statusLabel(String status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case "CREATED":
                return "待付款";
            case "PAID":
                return "待发货";
            case "SENT":
                return "待收货";
            case "COMPLETED":
                return "交易成功";
            case "CANCELLED":
                return "交易失败";
            case "GROUPING":
                return "待成团";
            default:
                return status;
        }
    }
}
