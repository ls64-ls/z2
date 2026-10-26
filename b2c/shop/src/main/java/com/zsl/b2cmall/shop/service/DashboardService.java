package com.zsl.b2cmall.shop.service;

import com.zsl.b2cmall.shop.dao.OrderMapper;
import com.zsl.b2cmall.shop.dao.RefundMapper;
import com.zsl.b2cmall.shop.entity.Message;
import com.zsl.b2cmall.shop.mapper.MessageMapper;
import com.zsl.b2cmall.shop.request.DashboardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RefundMapper refundMapper;

    @Autowired
    private MessageMapper messageMapper;

    /**
     * 获取 Dashboard 数据：6 个状态卡片 + 24 小时交易趋势 + 4 项经营指标 + 消息通知
     */
    public DashboardVO getDashboardData(Integer shopId) {
        DashboardVO vo = new DashboardVO();

        // 1. 顶部订单状态卡片
        vo.setPendingPay(orderMapper.countByStatus(shopId, "CREATED"));
        vo.setPendingShip(orderMapper.countByStatus(shopId, "PAID"));
        vo.setPendingReceive(orderMapper.countByStatus(shopId, "SENT"));
        vo.setCompleted(orderMapper.countByStatus(shopId, "COMPLETED"));
        vo.setAfterSale(refundMapper.countPending());
        vo.setStockWarning(orderMapper.countStockWarning(shopId));

        // 2. 24 小时实时交易趋势
        List<Integer> hours = new ArrayList<>();
        List<BigDecimal> todayAmounts = new ArrayList<>();
        List<BigDecimal> yesterdayAmounts = new ArrayList<>();
        Map<Integer, BigDecimal> todayMap = hourlyMap(shopId, "+0 day");
        Map<Integer, BigDecimal> yesterdayMap = hourlyMap(shopId, "-1 day");
        for (int h = 0; h < 24; h++) {
            hours.add(h);
            todayAmounts.add(todayMap.getOrDefault(h, BigDecimal.ZERO));
            yesterdayAmounts.add(yesterdayMap.getOrDefault(h, BigDecimal.ZERO));
        }
        vo.setHours(hours);
        vo.setTodayAmounts(todayAmounts);
        vo.setYesterdayAmounts(yesterdayAmounts);

        // 3. 4 项核心经营指标（含同比昨日）
        vo.setMetrics(buildMetrics(shopId));

        // 4. 消息通知
        List<Message> messages = messageMapper.findByShopId(shopId);
        List<DashboardVO.MessageItem> items = new ArrayList<>();
        for (Message m : messages) {
            DashboardVO.MessageItem item = new DashboardVO.MessageItem();
            item.setId(m.getId());
            item.setTitle(m.getTitle());
            item.setContent(m.getContent());
            item.setMsgType(m.getMsgType());
            item.setIsRead(m.getIsRead());
            item.setOrderId(m.getOrderId());
            item.setOrderNo(m.getOrderNo());
            item.setCreatedAt(m.getCreatedAt() != null
                    ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(m.getCreatedAt()) : null);
            items.add(item);
        }
        vo.setMessages(items);
        return vo;
    }

    private Map<Integer, BigDecimal> hourlyMap(Integer shopId, String offset) {
        Map<Integer, BigDecimal> map = new HashMap<>();
        List<Map<String, Object>> rows = orderMapper.sumHourly(shopId, offset);
        for (Map<String, Object> row : rows) {
            Object h = row.get("h");
            Object amt = row.get("amt");
            if (h != null) {
                map.put(Integer.parseInt(h.toString()),
                        new BigDecimal(amt == null ? "0" : amt.toString()).setScale(2, RoundingMode.HALF_UP));
            }
        }
        return map;
    }

    private List<DashboardVO.MetricItem> buildMetrics(Integer shopId) {
        List<DashboardVO.MetricItem> metrics = new ArrayList<>();

        // 今日 / 昨日 基础数据
        int todayCustomers = orderMapper.countPaidCustomers(shopId, "+0 day");
        int yesterdayCustomers = orderMapper.countPaidCustomers(shopId, "-1 day");
        int todayOrders = orderMapper.countPaidOrders(shopId, "+0 day");
        int yesterdayOrders = orderMapper.countPaidOrders(shopId, "-1 day");
        BigDecimal todayAmount = money(orderMapper.sumPaidAmount(shopId, "+0 day"));
        BigDecimal yesterdayAmount = money(orderMapper.sumPaidAmount(shopId, "-1 day"));
        BigDecimal todayRefund = money(refundMapper.sumCompletedAmount("+0 day"));
        BigDecimal yesterdayRefund = money(refundMapper.sumCompletedAmount("-1 day"));

        // 客单价 = 今日总支付金额 ÷ 支付订单数
        BigDecimal todayAvg = todayOrders == 0 ? BigDecimal.ZERO
                : todayAmount.divide(new BigDecimal(todayOrders), 2, RoundingMode.HALF_UP);
        BigDecimal yesterdayAvg = yesterdayOrders == 0 ? BigDecimal.ZERO
                : yesterdayAmount.divide(new BigDecimal(yesterdayOrders), 2, RoundingMode.HALF_UP);

        metrics.add(metric("支付客户数", String.valueOf(todayCustomers),
                new BigDecimal(todayCustomers), new BigDecimal(yesterdayCustomers)));
        metrics.add(metric("支付订单数", String.valueOf(todayOrders),
                new BigDecimal(todayOrders), new BigDecimal(yesterdayOrders)));
        metrics.add(metric("退款金额 (元)", todayRefund.setScale(2, RoundingMode.HALF_UP).toString(),
                todayRefund, yesterdayRefund));
        metrics.add(metric("客单价 (元)", todayAvg.setScale(2, RoundingMode.HALF_UP).toString(),
                todayAvg, yesterdayAvg));
        return metrics;
    }

    private DashboardVO.MetricItem metric(String name, String value, BigDecimal today, BigDecimal yesterday) {
        BigDecimal rate;
        if (yesterday.compareTo(BigDecimal.ZERO) == 0) {
            // 昨日无数据：今日也为 0 时显示 0.00%，否则按需求显示 100%
            rate = today.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : new BigDecimal("100");
        } else {
            rate = today.subtract(yesterday)
                    .divide(yesterday, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }
        rate = rate.setScale(2, RoundingMode.HALF_UP);
        DashboardVO.MetricItem item = new DashboardVO.MetricItem();
        item.setName(name);
        item.setValue(value);
        item.setRate((rate.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + rate + "%");
        item.setUp(rate.compareTo(BigDecimal.ZERO) >= 0);
        return item;
    }

    private BigDecimal money(Double v) {
        return new BigDecimal(v == null ? "0" : v.toString()).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 标记消息已读
     */
    public void markRead(Integer id) {
        messageMapper.markRead(id);
    }
}
