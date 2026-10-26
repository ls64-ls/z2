package com.zsl.b2cmall.shop.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ProductPO implements Serializable {

    private Integer id;
    private Integer shopId;
    private String name;
    /** 商品分类（一级类目：数码/服装/食品/家居/虚拟服务/数字商品） */
    private String category;
    /** 兼容字段：等于销售价 */
    private BigDecimal price;
    /** 兼容字段：等于仓库库存 + 门店库存 */
    private Integer stock;
    /** 1=实物商品, 2=虚拟商品（电子卡券） */
    private Integer type;
    private String description;
    /** 0=下架, 1=上架 */
    private Integer status;
    private String createdAt;

    /** 市场价（划线原价） */
    private BigDecimal marketPrice;
    /** 销售价 */
    private BigDecimal salePrice;
    /** 真实销量 */
    private Integer sales;
    /** 注水销量 */
    private Integer fakeSales;
    /** 仓库库存 */
    private Integer warehouseStock;
    /** 门店库存 */
    private Integer storeStock;
    /** 配送方式，逗号分隔：快递配送/同城配送/用户自提 */
    private String deliveryTypes;
    /** 排序，数字越大越靠前 */
    private Integer sort;
    /** 商品类型：普通商品/秒杀商品 */
    private String productType;
    /** 库存预警阈值 */
    private Integer stockThreshold;
    /** 商品主图 */
    private String coverImage;

    /** 商品类别名称 */
    public String getCategoryLabel() {
        return type != null && type == 2 ? "虚拟商品" : "实物商品";
    }

    /** 总库存 */
    public Integer getTotalStock() {
        int wh = warehouseStock == null ? 0 : warehouseStock;
        int st = storeStock == null ? 0 : storeStock;
        return wh + st;
    }

    /** 是否库存预警 */
    public boolean isStockWarning() {
        Integer threshold = stockThreshold == null ? 0 : stockThreshold;
        return threshold > 0 && getTotalStock() < threshold;
    }
}
