package com.zsl.b2cmall.shop.request;

import lombok.Data;

import java.io.Serializable;

/**
 * B 端订单筛选条件
 */
@Data
public class OrderQueryVO implements Serializable {

    private Integer shopId;
    /** 订单编号（精确） */
    private String orderNo;
    private String orderType;
    private String orderCategory;
    /** 订单状态：CREATED/PAID/SENT/COMPLETED/CANCELLED/GROUPING */
    private String status;
    /** 收件人姓名（模糊） */
    private String receiverName;
    /** 联系电话（模糊） */
    private String phone;
    private String payType;
    private String afterSaleStatus;
    private String logisticsType;
    private String storeName;
    /** 下单开始日期 yyyy-MM-dd */
    private String startDate;
    /** 下单结束日期 yyyy-MM-dd */
    private String endDate;
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public int getOffset() {
        int p = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int s = pageSize == null || pageSize < 1 ? 10 : pageSize;
        return (p - 1) * s;
    }
}
