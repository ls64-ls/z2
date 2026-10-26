package com.zsl.b2cmall.shop.web;

import com.zsl.b2cmall.common.response.BaseResponseVO;
import com.zsl.b2cmall.shop.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @PostMapping("/register")
    public BaseResponseVO<Integer> register(@RequestParam String shopName,
                                            @RequestParam String adminAccount,
                                            @RequestParam String adminPassword) {
        Integer shopId = shopService.register(shopName, adminAccount, adminPassword);
        return BaseResponseVO.success(shopId);
    }
}
