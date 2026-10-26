package com.zsl.b2cmall.shop.request;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CreateProductRequestVO implements Serializable {

    /** 编辑时必填 */
    private Integer id;
    private Integer shopId;
    private String name;
    /** 商品分类（一级类目） */
    private String category;
    /** 兼容字段，等于销售价 */
    private BigDecimal price;
    /** 兼容字段，等于仓库库存 + 门店库存 */
    private Integer stock;
    /** 1=实物商品, 2=虚拟商品 */
    private Integer type;
    private String description;
    private Integer status;

    private BigDecimal marketPrice;
    private BigDecimal salePrice;
    private Integer sales;
    private Integer fakeSales;
    private Integer warehouseStock;
    private Integer storeStock;
    private String deliveryTypes;
    private Integer sort;
    /** 普通商品/秒杀商品 */
    private String productType;
    private Integer stockThreshold;
    private String coverImage;
}
