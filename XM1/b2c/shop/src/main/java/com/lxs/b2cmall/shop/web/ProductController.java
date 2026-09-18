package com.lxs.b2cmall.shop.web;

import com.lxs.b2cmall.common.response.BaseResponseVO;
import com.lxs.b2cmall.shop.request.CreateProductRequestVO;
import com.lxs.b2cmall.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 新建商品
     * 使用模板方法模式：实物商品校验库存，虚拟商品校验合规
     */
    @PostMapping("/create")
    public BaseResponseVO createProduct(@RequestBody CreateProductRequestVO request) {
        return productService.createProduct(request);
    }

    /**
     * 商品列表
     */
    @GetMapping("/list")
    public BaseResponseVO listProducts(@RequestParam Integer shopId) {
        return productService.listProducts(shopId);
    }
}
