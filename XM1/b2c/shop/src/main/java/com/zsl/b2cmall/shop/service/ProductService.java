package com.zsl.b2cmall.shop.service;

import com.zsl.b2cmall.common.response.BaseResponseVO;
import com.zsl.b2cmall.shop.dao.ProductMapper;
import com.zsl.b2cmall.shop.entity.ProductPO;
import com.zsl.b2cmall.shop.request.CreateProductRequestVO;
import com.zsl.b2cmall.shop.service.template.AbstractProductCreateTemplate;
import com.zsl.b2cmall.shop.service.template.impl.PhysicalProductCreateTemplate;
import com.zsl.b2cmall.shop.service.template.impl.VirtualProductCreateTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

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
            // 虚拟商品
            template = virtualTemplate;
        } else {
            // 默认实物商品
            template = physicalTemplate;
        }
        return template.createProduct(request);
    }

    /**
     * 查询店铺的商品列表
     */
    public BaseResponseVO listProducts(Integer shopId) {
        List<ProductPO> products = productMapper.findByShopId(shopId);
        return BaseResponseVO.success(products);
    }
}
