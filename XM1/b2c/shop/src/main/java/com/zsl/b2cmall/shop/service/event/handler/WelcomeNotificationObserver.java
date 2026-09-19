package com.zsl.b2cmall.shop.service.event.handler;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.zsl.b2cmall.shop.entity.Message;
import com.zsl.b2cmall.shop.mapper.MessageMapper;
import com.zsl.b2cmall.shop.service.event.ShopRegisterEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 欢迎通知观察者：发送站内信
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WelcomeNotificationObserver implements InitializingBean {

    private final EventBus eventBus;
    private final MessageMapper messageMapper;

    @Subscribe
    public void onShopRegister(ShopRegisterEvent event) {
        log.info("[欢迎通知] 收到注册成功事件: {}, 发送欢迎站内信", event);

        Message message = new Message();
        message.setShopId(event.getShopId());
        message.setSenderId(null);
        message.setTitle("欢迎入驻");
        message.setContent("欢迎您入驻本平台，祝您生意兴隆！");
        message.setMsgType(1);
        message.setIsRead(false);
        Date now = new Date();
        message.setCreatedAt(now);
        message.setUpdatedAt(now);

        messageMapper.insert(message);
        log.info("[欢迎通知] 店铺欢迎消息已发送, shopId: {}", event.getShopId());
    }

    @Override
    public void afterPropertiesSet() {
        eventBus.register(this);
    }
}
