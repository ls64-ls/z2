package com.zsl.b2cmall.shop.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderItemPO implements Serializable {

    private Integer id;
    private Integer orderId;
    private Integer productId;
    private String productName;
    private String productImage;
    private BigDecimal unitPrice;
    private Integer quantity;
    /** 是否赠品 0/1 */
    private Integer isGift;

    public BigDecimal getSubtotal() {
        if (unitPrice == null) {
            return BigDecimal.ZERO;
        }
        int q = quantity == null ? 0 : quantity;
        return unitPrice.multiply(new BigDecimal(q));
    }
}
