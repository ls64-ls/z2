package com.zsl.b2cmall.shop.service.event.handler;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.zsl.b2cmall.common.response.BaseResponseVO;
import com.zsl.b2cmall.shop.feign.EmployeeFeignClient;
import com.zsl.b2cmall.shop.feign.request.AddEmployeeRequestVO;
import com.zsl.b2cmall.shop.service.event.ShopRegisterEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 账号初始化观察者：生成默认头像、分配默认角色权限、初始化用户档案
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountInitObserver implements InitializingBean {

    private final EventBus eventBus;
    private final EmployeeFeignClient employeeFeignClient;

    @Subscribe
    public void onShopRegister(ShopRegisterEvent event) {
        log.info("[账号初始化] 收到注册成功事件: {}, 调用 employee service 初始化账户", event);

        AddEmployeeRequestVO req = new AddEmployeeRequestVO();
        req.setShopId(event.getShopId());
        req.setUsername(event.getAdminAccount());
        req.setPassword(event.getAdminPwd());

        BaseResponseVO<Integer> resp = employeeFeignClient.save(req);
        if (!Objects.equals(resp.getStatus(), 200)) {
            log.error("[账号初始化] 保存用户失败");
            return;
        }
        log.info("[账号初始化] 保存员工成功 id: {}", resp.getData());
        log.info("[账号初始化] 已生成默认头像、分配默认角色权限、初始化用户档案");
    }

    @Override
    public void afterPropertiesSet() {
        eventBus.register(this);
    }
}
