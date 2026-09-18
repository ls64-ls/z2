package com.lxs.b2cmall.shop.service;

import com.lxs.b2cmall.shop.entity.Message;
import com.lxs.b2cmall.shop.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;

    public void sendSystemMessage(Integer shopId, String title, String content) {
        Message message = new Message();
        message.setShopId(shopId);
        message.setSenderId(null);
        message.setTitle(title);
        message.setContent(content);
        message.setMsgType(1);
        message.setIsRead(false);
        Date now = new Date();
        message.setCreatedAt(now);
        message.setUpdatedAt(now);
        messageMapper.insert(message);
    }
}
