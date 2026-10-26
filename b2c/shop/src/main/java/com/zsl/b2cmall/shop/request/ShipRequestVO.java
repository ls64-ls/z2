package com.zsl.b2cmall.shop.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 发货请求（单条 / 批量）
 */
@Data
public class ShipRequestVO implements Serializable {

    private Integer orderId;
    private List<Integer> orderIds;
    /** 物流类型：普通快递/同城配送 */
    private String logisticsType;
    /** 物流单号 */
    private String deliveryNo;
}
