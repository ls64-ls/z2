package com.zsl.b2cmall.shop.request;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品筛选条件
 */
@Data
public class ProductQueryVO implements Serializable {

    private Integer shopId;
    /** 商品名称模糊 */
    private String name;
    /** 商品类型：普通商品/秒杀商品 */
    private String productType;
    /** 商品类别：实物商品/虚拟商品 */
    private String categoryLabel;
    /** 状态：1上架 0下架 */
    private Integer status;
    /** 配送方式，多选，逗号分隔 */
    private String deliveryTypes;
    /** 商品分类 */
    private String category;
    /** 仅看库存预警 */
    private Integer stockWarning;
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    /** 配送方式拆分（供 SQL foreach 使用） */
    private List<String> deliveryTypeList = new ArrayList<>();

    public int getOffset() {
        int p = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int s = pageSize == null || pageSize < 1 ? 10 : pageSize;
        return (p - 1) * s;
    }
}
