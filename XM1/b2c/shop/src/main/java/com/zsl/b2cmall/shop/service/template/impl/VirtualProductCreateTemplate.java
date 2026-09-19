package com.zsl.b2cmall.shop.service.template.impl;

import com.zsl.b2cmall.common.response.BaseResponseVO;
import com.zsl.b2cmall.shop.dao.ProductMapper;
import com.zsl.b2cmall.shop.entity.ProductPO;
import com.zsl.b2cmall.shop.request.CreateProductRequestVO;
import com.zsl.b2cmall.shop.service.template.AbstractProductCreateTemplate;
import org.springframework.stereotype.Component;

/**
 * 虚拟商品创建模板
 * 差异化：不需要库存（设为0），重点校验内容合规
 */
@Component
public class VirtualProductCreateTemplate extends AbstractProductCreateTemplate {

    public VirtualProductCreateTemplate(ProductMapper productMapper) {
        super(productMapper);
    }

    @Override
    protected BaseResponseVO validatePriceAndStock(CreateProductRequestVO request) {
        // 虚拟商品不需要库存，统一设为0
        request.setStock(0);
        return BaseResponseVO.success();
    }

    @Override
    protected BaseResponseVO reviewContent(CreateProductRequestVO request) {
        // 虚拟商品重点校验合规性
        String desc = request.getDescription();
        if (desc == null || desc.trim().isEmpty()) {
            return BaseResponseVO.fail("虚拟商品必须填写使用说明/服务描述");
        }
        // 虚拟商品审核更严格，检查违禁词和有效期说明
        String[] forbidden = {"赌博", "色情", "违法", "高仿", "假货"};
        for (String word : forbidden) {
            if (desc.contains(word)) {
                return BaseResponseVO.fail("虚拟商品描述包含违禁词【" + word + "】，审核不通过");
            }
        }
        if (!desc.contains("有效期") && !desc.contains("期限") && !desc.contains("售后")) {
            return BaseResponseVO.fail("虚拟商品描述必须包含有效期/售后说明");
        }
        return BaseResponseVO.success();
    }

    @Override
    protected void postProcess(ProductPO product) {
        // 虚拟商品后置处理：标记自动发货
        log.info("虚拟商品后置处理：配置自动发货，productId={}", product.getId());
    }

    @Override
    protected Integer getProductType() {
        return 2;
    }

    @Override
    protected String getProductTypeName() {
        return "虚拟商品";
    }
}
