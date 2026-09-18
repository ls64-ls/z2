package com.lxs.b2cmall.shop.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ProductPO implements Serializable {

    private Integer id;
    private Integer shopId;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer stock;
    /** 1=实物商品, 2=虚拟商品 */
    private Integer type;
    private String description;
    /** 0=下架, 1=上架 */
    private Integer status;
    private String createdAt;
}
