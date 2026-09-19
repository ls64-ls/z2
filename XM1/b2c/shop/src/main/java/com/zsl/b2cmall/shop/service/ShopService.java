package com.zsl.b2cmall.shop.service;

import com.google.common.eventbus.EventBus;
import com.zsl.b2cmall.shop.entity.Shop;
import com.zsl.b2cmall.shop.mapper.ShopMapper;
import com.zsl.b2cmall.shop.service.event.ShopRegisterEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopMapper shopMapper;
    private final EventBus eventBus;

    @Transactional(rollbackFor = Exception.class)
    public Integer register(String shopName, String adminAccount, String adminPassword) {
        Shop shop = new Shop();
        shop.setShopName(shopName);
        shop.setAdminAccount(adminAccount);
        shop.setAdminPassword(adminPassword);
        shop.setLogoUrl(null);
        shop.setStatus(1);
        Date now = new Date();
        shop.setCreatedAt(now);
        shop.setUpdatedAt(now);

        shopMapper.insert(shop);

        // 发布店铺注册成功事件，触发后续初始化
        eventBus.post(ShopRegisterEvent.builder()
                .shopId(shop.getId())
                .adminAccount(adminAccount)
                .adminPwd(adminPassword)
                .build());

        return shop.getId();
    }
}
