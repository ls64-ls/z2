package com.lxs.b2cmall.shop.service.template.impl;

import com.lxs.b2cmall.common.response.BaseResponseVO;
import com.lxs.b2cmall.shop.dao.ProductMapper;
import com.lxs.b2cmall.shop.request.CreateProductRequestVO;
import com.lxs.b2cmall.shop.service.template.AbstractProductCreateTemplate;
import org.springframework.stereotype.Component;

/**
 * 实物商品创建模板
 * 差异化：必须校验库存 > 0
 */
@Component
public class PhysicalProductCreateTemplate extends AbstractProductCreateTemplate {

    public PhysicalProductCreateTemplate(ProductMapper productMapper) {
        super(productMapper);
    }

    @Override
    protected BaseResponseVO validatePriceAndStock(CreateProductRequestVO request) {
        if (request.getStock() == null || request.getStock() <= 0) {
            return BaseResponseVO.fail("实物商品库存必须大于0");
        }
        return BaseResponseVO.success();
    }

    @Override
    protected BaseResponseVO reviewContent(CreateProductRequestVO request) {
        // 实物商品内容审核：检查是否包含违禁词
        String desc = request.getDescription();
        if (desc != null && containsForbiddenWords(desc)) {
            return BaseResponseVO.fail("商品描述包含违禁词，审核不通过");
        }
        return BaseResponseVO.success();
    }

    @Override
    protected Integer getProductType() {
        return 1;
    }

    @Override
    protected String getProductTypeName() {
        return "实物商品";
    }

    private boolean containsForbiddenWords(String text) {
        String[] forbidden = {"假货", "高仿", "违禁品"};
        for (String word : forbidden) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
