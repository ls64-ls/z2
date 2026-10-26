package com.zsl.b2cmall.shop.service;

import com.zsl.b2cmall.common.response.BaseResponseVO;
import com.zsl.b2cmall.shop.dao.OrderMapper;
import com.zsl.b2cmall.shop.dao.ProductMapper;
import com.zsl.b2cmall.shop.entity.ProductPO;
import com.zsl.b2cmall.shop.request.BatchUpdateRequestVO;
import com.zsl.b2cmall.shop.request.CreateProductRequestVO;
import com.zsl.b2cmall.shop.request.PageResult;
import com.zsl.b2cmall.shop.request.ProductQueryVO;
import com.zsl.b2cmall.shop.service.template.AbstractProductCreateTemplate;
import com.zsl.b2cmall.shop.service.template.impl.PhysicalProductCreateTemplate;
import com.zsl.b2cmall.shop.service.template.impl.VirtualProductCreateTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private static final Map<String, String> BATCH_COLUMNS = new HashMap<>();

    static {
        BATCH_COLUMNS.put("status", "status");
        BATCH_COLUMNS.put("productType", "product_type");
        BATCH_COLUMNS.put("category", "category");
        BATCH_COLUMNS.put("deliveryTypes", "delivery_types");
        BATCH_COLUMNS.put("sort", "sort");
        BATCH_COLUMNS.put("warehouseStock", "warehouse_stock");
        BATCH_COLUMNS.put("storeStock", "store_stock");
        BATCH_COLUMNS.put("stockThreshold", "stock_threshold");
    }

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PhysicalProductCreateTemplate physicalTemplate;

    @Autowired
    private VirtualProductCreateTemplate virtualTemplate;

    /**
     * 创建商品：根据类型选择对应模板（模板方法模式）
     */
    public BaseResponseVO createProduct(CreateProductRequestVO request) {
        AbstractProductCreateTemplate template;
        if (request.getType() != null && request.getType() == 2) {
            template = virtualTemplate;
        } else {
            template = physicalTemplate;
        }
        return template.createProduct(request);
    }

    /**
     * 查询店铺的商品列表（兼容旧接口）
     */
    public BaseResponseVO listProducts(Integer shopId) {
        List<ProductPO> products = productMapper.findByShopId(shopId);
        return BaseResponseVO.success(products);
    }

    /**
     * 商品管理分页查询（多条件筛选）
     */
    public BaseResponseVO page(ProductQueryVO query) {
        if (query.getShopId() == null) {
            return BaseResponseVO.fail("店铺ID不能为空");
        }
        if (query.getPageNum() == null || query.getPageNum() < 1) {
            query.setPageNum(1);
        }
        if (query.getPageSize() == null || query.getPageSize() < 1) {
            query.setPageSize(10);
        }
        query.setDeliveryTypeList(split(query.getDeliveryTypes()));
        List<ProductPO> list = productMapper.queryPage(query);
        long total = productMapper.countQuery(query);
        return BaseResponseVO.success(new PageResult<>(list, total, query.getPageNum(), query.getPageSize()));
    }

    /**
     * 商品详情
     */
    public BaseResponseVO detail(Integer id) {
        ProductPO product = productMapper.findById(id);
        if (product == null) {
            return BaseResponseVO.fail("商品不存在");
        }
        return BaseResponseVO.success(product);
    }

    /**
     * 编辑商品
     */
    public BaseResponseVO update(CreateProductRequestVO request) {
        if (request.getId() == null) {
            return BaseResponseVO.fail("商品ID不能为空");
        }
        ProductPO exist = productMapper.findById(request.getId());
        if (exist == null) {
            return BaseResponseVO.fail("商品不存在");
        }
        ProductPO product = convert(request, exist);
        productMapper.update(product);
        return BaseResponseVO.success("商品更新成功", product.getId());
    }

    /**
     * 上下架
     */
    public BaseResponseVO changeStatus(Integer id, Integer shopId, Integer status) {
        int rows = productMapper.updateStatusByShop(id, shopId, status);
        if (rows == 0) {
            return BaseResponseVO.fail("商品不存在或不属于当前店铺");
        }
        return BaseResponseVO.success(status != null && status == 1 ? "商品已上架" : "商品已下架", null);
    }

    /**
     * 删除商品：已产生订单的商品禁止删除
     */
    public BaseResponseVO delete(Integer id, Integer shopId) {
        ProductPO product = productMapper.findByIdAndShop(id, shopId);
        if (product == null) {
            return BaseResponseVO.fail("商品不存在或不属于当前店铺");
        }
        if (orderMapper.countByProductId(id) > 0) {
            return BaseResponseVO.fail("该商品已产生订单，禁止删除");
        }
        productMapper.deleteById(id);
        return BaseResponseVO.success("商品已删除", null);
    }

    /**
     * 批量设置：批量修改状态、配送方式、分类、排序、库存等
     */
    public BaseResponseVO batchUpdate(BatchUpdateRequestVO request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return BaseResponseVO.fail("请先勾选商品");
        }
        String column = BATCH_COLUMNS.get(request.getField());
        if (column == null) {
            return BaseResponseVO.fail("不支持的批量修改字段：" + request.getField());
        }
        Object value = request.getValue();
        if ("status".equals(request.getField()) || "sort".equals(request.getField())
                || "warehouseStock".equals(request.getField()) || "storeStock".equals(request.getField())
                || "stockThreshold".equals(request.getField())) {
            value = Integer.valueOf(String.valueOf(value));
        }
        productMapper.batchUpdate(request.getIds(), column, value);
        return BaseResponseVO.success("批量设置成功", request.getIds().size());
    }

    /**
     * 筛选下拉选项
     */
    public BaseResponseVO options(Integer shopId) {
        Map<String, Object> data = new HashMap<>();
        List<String> categories = new ArrayList<>();
        for (ProductPO p : productMapper.findByShopId(shopId)) {
            if (p.getCategory() != null && !categories.contains(p.getCategory())) {
                categories.add(p.getCategory());
            }
        }
        data.put("categories", categories);
        data.put("productTypes", Arrays.asList("普通商品", "秒杀商品"));
        data.put("categoryLabels", Arrays.asList("实物商品", "虚拟商品"));
        data.put("deliveryTypes", Arrays.asList("同城配送", "用户自提", "快递配送"));
        data.put("orderTypes", Arrays.asList("普通订单", "秒杀订单", "拼团订单"));
        data.put("orderCategories", Arrays.asList("普通订单", "电子卡券订单"));
        data.put("payTypes", Arrays.asList("微信支付", "支付宝", "余额支付"));
        data.put("afterSaleStatuses", Arrays.asList("暂无售后", "退款申请中", "退款完成"));
        data.put("logisticsTypes", Arrays.asList("普通快递", "同城配送"));
        return BaseResponseVO.success(data);
    }

    /**
     * C 端商品分类（去重）
     */
    public BaseResponseVO mallCategories() {
        return BaseResponseVO.success(productMapper.distinctCategories());
    }

    /**
     * C 端商品列表（仅在售商品）
     */
    public BaseResponseVO mallList(String category, String sort) {
        return BaseResponseVO.success(productMapper.mallList(category, sort));
    }

    /**
     * C 端商品详情
     */
    public BaseResponseVO mallDetail(Integer id) {
        ProductPO product = productMapper.findById(id);
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            return BaseResponseVO.fail("商品不存在或已下架");
        }
        return BaseResponseVO.success(product);
    }

    private List<String> split(String value) {
        List<String> list = new ArrayList<>();
        if (value != null && !value.trim().isEmpty()) {
            for (String s : value.split(",")) {
                if (!s.trim().isEmpty()) {
                    list.add(s.trim());
                }
            }
        }
        return list;
    }

    private ProductPO convert(CreateProductRequestVO req, ProductPO product) {
        product.setName(req.getName());
        product.setCategory(req.getCategory());
        product.setDescription(req.getDescription());
        if (req.getType() != null) {
            product.setType(req.getType());
        }
        BigDecimal salePrice = req.getSalePrice() != null ? req.getSalePrice()
                : (req.getPrice() != null ? req.getPrice() : product.getSalePrice());
        product.setSalePrice(salePrice);
        product.setPrice(salePrice);
        product.setMarketPrice(req.getMarketPrice() != null ? req.getMarketPrice() : product.getMarketPrice());
        product.setFakeSales(req.getFakeSales() != null ? req.getFakeSales() : product.getFakeSales());
        product.setWarehouseStock(req.getWarehouseStock() != null ? req.getWarehouseStock() : product.getWarehouseStock());
        product.setStoreStock(req.getStoreStock() != null ? req.getStoreStock() : product.getStoreStock());
        int wh = product.getWarehouseStock() == null ? 0 : product.getWarehouseStock();
        int st = product.getStoreStock() == null ? 0 : product.getStoreStock();
        product.setStock(req.getStock() != null ? req.getStock() : wh + st);
        product.setDeliveryTypes(req.getDeliveryTypes() != null ? req.getDeliveryTypes() : product.getDeliveryTypes());
        product.setSort(req.getSort() != null ? req.getSort() : product.getSort());
        product.setProductType(req.getProductType() != null ? req.getProductType() : product.getProductType());
        product.setStockThreshold(req.getStockThreshold() != null ? req.getStockThreshold() : product.getStockThreshold());
        product.setCoverImage(req.getCoverImage() != null ? req.getCoverImage() : product.getCoverImage());
        product.setSales(req.getSales() != null ? req.getSales() : product.getSales());
        return product;
    }
}
