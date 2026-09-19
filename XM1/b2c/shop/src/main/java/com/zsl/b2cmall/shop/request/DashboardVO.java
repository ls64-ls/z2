package com.zsl.b2cmall.shop.request;

import com.zsl.b2cmall.shop.entity.OrderPO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Dashboard 数据展示对象
 * 包含消息通知和订单统计数据
 */
@Data
public class DashboardVO implements Serializable {

    /** 订单总数 */
    private Integer totalOrders;
    /** 待支付订单数 */
    private Integer pendingPayOrders;
    /** 已支付订单数 */
    private Integer paidOrders;
    /** 已发货订单数 */
    private Integer shippedOrders;
    /** 已完成订单数 */
    private Integer completedOrders;
    /** 销售额（已支付及以上状态） */
    private BigDecimal totalSales;
    /** 消息通知列表 */
    private List<MessageItem> messages;
    /** 订单列表 */
    private List<OrderPO> orders;

    @Data
    public static class MessageItem implements Serializable {
        private Integer id;
        private String title;
        private String content;
        private Integer msgType;
        private Boolean isRead;
        private String createdAt;
    }
}
