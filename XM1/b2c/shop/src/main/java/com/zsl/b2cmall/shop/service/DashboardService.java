package com.zsl.b2cmall.shop.service;

import com.zsl.b2cmall.shop.dao.OrderMapper;
import com.zsl.b2cmall.shop.entity.Message;
import com.zsl.b2cmall.shop.mapper.MessageMapper;
import com.zsl.b2cmall.shop.request.DashboardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private MessageMapper messageMapper;

    /**
     * 获取 Dashboard 数据：消息通知 + 订单统计
     */
    public DashboardVO getDashboardData(Integer shopId) {
        DashboardVO vo = new DashboardVO();

        // 订单统计
        vo.setTotalOrders(orderMapper.countByShopId(shopId));
        vo.setPendingPayOrders(orderMapper.countByShopIdAndStatus(shopId, 0));
        vo.setPaidOrders(orderMapper.countByShopIdAndStatus(shopId, 1));
        vo.setShippedOrders(orderMapper.countByShopIdAndStatus(shopId, 2));
        vo.setCompletedOrders(orderMapper.countByShopIdAndStatus(shopId, 3));

        Double sales = orderMapper.sumSalesByShopId(shopId);
        BigDecimal salesBig = BigDecimal.valueOf(sales != null ? sales : 0.0);
        vo.setTotalSales(salesBig.setScale(2, BigDecimal.ROUND_HALF_UP));

        // 消息通知
        List<Message> messages = messageMapper.findByShopId(shopId);
        List<DashboardVO.MessageItem> items = new ArrayList<>();
        for (Message m : messages) {
            DashboardVO.MessageItem item = new DashboardVO.MessageItem();
            item.setId(m.getId());
            item.setTitle(m.getTitle());
            item.setContent(m.getContent());
            item.setMsgType(m.getMsgType());
            item.setIsRead(m.getIsRead());
            item.setCreatedAt(m.getCreatedAt() != null ? m.getCreatedAt().toString() : null);
            items.add(item);
        }
        vo.setMessages(items);

        // 订单列表
        vo.setOrders(orderMapper.findByShopId(shopId));

        return vo;
    }
}
