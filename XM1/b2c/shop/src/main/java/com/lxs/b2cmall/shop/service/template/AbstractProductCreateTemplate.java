package com.lxs.b2cmall.shop.service.template;

import com.lxs.b2cmall.shop.dao.ProductMapper;
import com.lxs.b2cmall.shop.entity.ProductPO;
import com.lxs.b2cmall.shop.request.CreateProductRequestVO;
import com.lxs.b2cmall.common.response.BaseResponseVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 商品创建抽象模板（模板方法模式）
 * 定义创建商品的固定流程骨架，子类实现差异化步骤
 *
 * 流程：参数校验 → 类目归属校验 → 价格库存校验 → 内容合规审核 → 保存商品 → 上架 → 后置处理
 */
public abstract class AbstractProductCreateTemplate {

    protected static final Logger log = LoggerFactory.getLogger(AbstractProductCreateTemplate.class);

    protected final ProductMapper productMapper;

    protected AbstractProductCreateTemplate(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    /**
     * 模板方法：创建商品的完整流程（final，不允许子类修改流程顺序）
     */
    public final BaseResponseVO createProduct(CreateProductRequestVO request) {
        log.info("开始创建商品，类型={}", getProductTypeName());

        // 1. 参数校验
        BaseResponseVO paramCheck = validateParams(request);
        if (paramCheck.getStatus() != 200) {
            return paramCheck;
        }

        // 2. 类目归属校验
        BaseResponseVO categoryCheck = validateCategory(request);
        if (categoryCheck.getStatus() != 200) {
            return categoryCheck;
        }

        // 3. 价格库存校验（子类实现）
        BaseResponseVO stockCheck = validatePriceAndStock(request);
        if (stockCheck.getStatus() != 200) {
            return stockCheck;
        }

        // 4. 内容合规审核（子类实现）
        BaseResponseVO reviewCheck = reviewContent(request);
        if (reviewCheck.getStatus() != 200) {
            return reviewCheck;
        }

        // 5. 保存商品信息
        ProductPO product = saveProduct(request);

        // 6. 上架
        putOnSale(product);

        // 7. 后置处理（钩子方法）
        postProcess(product);

        log.info("商品创建成功，id={}, name={}", product.getId(), product.getName());
        return BaseResponseVO.success("商品创建成功", product.getId());
    }

    /** 1. 参数校验（通用） */
    protected BaseResponseVO validateParams(CreateProductRequestVO request) {
        if (request.getShopId() == null) {
            return BaseResponseVO.fail("店铺ID不能为空");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return BaseResponseVO.fail("商品名称不能为空");
        }
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            return BaseResponseVO.fail("商品类目不能为空");
        }
        if (request.getPrice() == null || request.getPrice().doubleValue() <= 0) {
            return BaseResponseVO.fail("商品价格必须大于0");
        }
        return BaseResponseVO.success();
    }

    /** 2. 类目归属校验（通用） */
    protected BaseResponseVO validateCategory(CreateProductRequestVO request) {
        String category = request.getCategory();
        // 模拟类目白名单校验
        String[] allowedCategories = {"服装", "数码", "食品", "家居", "虚拟服务", "数字商品"};
        for (String c : allowedCategories) {
            if (c.equals(category)) {
                return BaseResponseVO.success();
            }
        }
        return BaseResponseVO.fail("类目【" + category + "】不在允许的类目范围内");
    }

    /** 3. 价格库存校验（抽象方法，子类实现） */
    protected abstract BaseResponseVO validatePriceAndStock(CreateProductRequestVO request);

    /** 4. 内容合规审核（抽象方法，子类实现） */
    protected abstract BaseResponseVO reviewContent(CreateProductRequestVO request);

    /** 5. 保存商品信息（通用） */
    protected ProductPO saveProduct(CreateProductRequestVO request) {
        ProductPO product = new ProductPO();
        product.setShopId(request.getShopId());
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock() != null ? request.getStock() : 0);
        product.setType(getProductType());
        product.setDescription(request.getDescription());
        product.setStatus(0); // 初始为下架状态
        productMapper.insert(product);
        log.info("商品已保存，id={}", product.getId());
        return product;
    }

    /** 6. 上架（通用） */
    protected void putOnSale(ProductPO product) {
        productMapper.updateStatus(product.getId(), 1);
        product.setStatus(1);
        log.info("商品已上架，id={}", product.getId());
    }

    /** 7. 后置处理（钩子方法，子类可选覆盖） */
    protected void postProcess(ProductPO product) {
        // 默认空实现，子类可覆盖
    }

    /** 获取商品类型编码 */
    protected abstract Integer getProductType();

    /** 获取商品类型名称 */
    protected abstract String getProductTypeName();
}
