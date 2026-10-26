package com.zsl.b2cmall.shop.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商品批量设置 / 批量上下架请求
 */
@Data
public class BatchUpdateRequestVO implements Serializable {

    private List<Integer> ids;
    /** 批量修改字段：status/productType/category/deliveryTypes/sort/warehouseStock/storeStock */
    private String field;
    private Object value;
}
