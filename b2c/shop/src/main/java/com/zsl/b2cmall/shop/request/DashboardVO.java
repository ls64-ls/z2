package com.zsl.b2cmall.shop.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Dashboard 数据展示对象：6 个状态卡片 + 24 小时交易趋势 + 4 项经营指标 + 消息通知
 */
@Data
public class DashboardVO implements Serializable {

    /** 待付款 */
    private Integer pendingPay = 0;
    /** 待发货 */
    private Integer pendingShip = 0;
    /** 待收货 */
    private Integer pendingReceive = 0;
    /** 已完成 */
    private Integer completed = 0;
    /** 退款/售后 */
    private Integer afterSale = 0;
    /** 库存预警 */
    private Integer stockWarning = 0;

    /** X 轴小时（0~23） */
    private List<Integer> hours;
    /** 今日支付金额（按小时） */
    private List<BigDecimal> todayAmounts;
    /** 昨日支付金额（按小时） */
    private List<BigDecimal> yesterdayAmounts;

    /** 4 项核心经营指标 */
    private List<MetricItem> metrics;

    /** 消息通知 */
    private List<MessageItem> messages;

    @Data
    public static class MetricItem implements Serializable {
        /** 指标名称 */
        private String name;
        /** 当前数值 */
        private String value;
        /** 同比昨日变化率（已格式化，如 +100.00%） */
        private String rate;
        /** 是否为上涨 */
        private boolean up;
    }

    @Data
    public static class MessageItem implements Serializable {
        private Integer id;
        private String title;
        private String content;
        private Integer msgType;
        private Boolean isRead;
        private Integer orderId;
        private String orderNo;
        private String createdAt;
    }
}
