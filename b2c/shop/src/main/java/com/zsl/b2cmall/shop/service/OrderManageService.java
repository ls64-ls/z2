package com.zsl.b2cmall.shop.service;

import com.zsl.b2cmall.common.response.BaseResponseVO;
import com.zsl.b2cmall.shop.dao.OrderItemMapper;
import com.zsl.b2cmall.shop.dao.OrderMapper;
import com.zsl.b2cmall.shop.dao.RefundMapper;
import com.zsl.b2cmall.shop.entity.OrderItemPO;
import com.zsl.b2cmall.shop.entity.OrderPO;
import com.zsl.b2cmall.shop.entity.RefundPO;
import com.zsl.b2cmall.shop.request.OrderGroupVO;
import com.zsl.b2cmall.shop.request.OrderQueryVO;
import com.zsl.b2cmall.shop.request.PageResult;
import com.zsl.b2cmall.shop.request.ShipRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * B 端订单管理 + C 端我的订单
 */
@Service
public class OrderManageService {

    private static final String DEFAULT_SHOP_NAME = "ZSL 甄选旗舰店";

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private RefundMapper refundMapper;

    @Autowired
    private ExcelService excelService;

    // ==================== B 端 ====================

    public BaseResponseVO page(OrderQueryVO query) {
        if (query.getShopId() == null) {
            return BaseResponseVO.fail("店铺ID不能为空");
        }
        List<OrderPO> orders = orderMapper.queryPage(query);
        long total = orderMapper.countQuery(query);
        List<OrderGroupVO> groups = toGroups(orders);
        return BaseResponseVO.success(new PageResult<>(groups, total, query.getPageNum(), query.getPageSize()));
    }

    public BaseResponseVO detail(Integer id) {
        OrderPO order = orderMapper.findById(id);
        if (order == null) {
            return BaseResponseVO.fail("订单不存在");
        }
        List<OrderGroupVO> groups = toGroups(java.util.Collections.singletonList(order));
        OrderGroupVO vo = groups.get(0);
        RefundPO refund = refundMapper.findByOrderId(id);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("order", vo);
        data.put("refund", refund);
        return BaseResponseVO.success(data);
    }

    /** 状态快捷标签栏计数 */
    public BaseResponseVO statusCounts(Integer shopId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("ALL", orderMapper.countAll(shopId));
        data.put("CREATED", orderMapper.countByStatus(shopId, "CREATED"));
        data.put("PAID", orderMapper.countByStatus(shopId, "PAID"));
        data.put("SENT", orderMapper.countByStatus(shopId, "SENT"));
        data.put("COMPLETED", orderMapper.countByStatus(shopId, "COMPLETED"));
        data.put("CANCELLED", orderMapper.countByStatus(shopId, "CANCELLED"));
        data.put("GROUPING", orderMapper.countByStatus(shopId, "GROUPING"));
        return BaseResponseVO.success(data);
    }

    /** 单条 / 批量发货 */
    public BaseResponseVO ship(ShipRequestVO request) {
        List<Integer> ids = new ArrayList<>();
        if (request.getOrderId() != null) {
            ids.add(request.getOrderId());
        }
        if (request.getOrderIds() != null) {
            ids.addAll(request.getOrderIds());
        }
        if (ids.isEmpty()) {
            return BaseResponseVO.fail("请先选择订单");
        }
        if (request.getLogisticsType() == null || request.getLogisticsType().trim().isEmpty()) {
            return BaseResponseVO.fail("请选择物流类型");
        }
        String deliveryNo = request.getDeliveryNo();
        if (deliveryNo == null || deliveryNo.trim().isEmpty()) {
            deliveryNo = "SF" + System.currentTimeMillis();
        }
        int success = 0;
        for (Integer id : ids) {
            success += orderMapper.markSent(id, request.getLogisticsType(), deliveryNo);
        }
        if (success == 0) {
            return BaseResponseVO.fail("所选订单当前状态不允许发货");
        }
        return BaseResponseVO.success("成功发货 " + success + " 单", success);
    }

    public void export(OrderQueryVO query, HttpServletResponse response) {
        List<OrderPO> orders = orderMapper.queryAll(query);
        excelService.exportOrders(orders, response);
    }

    /** 退款信息 */
    public BaseResponseVO refund(Integer orderId) {
        RefundPO refund = refundMapper.findByOrderId(orderId);
        if (refund == null) {
            return BaseResponseVO.fail("该订单暂无退款售后记录");
        }
        return BaseResponseVO.success(refund);
    }

    // ==================== C 端 ====================

    public BaseResponseVO customerOrders(Integer userId, String status, String orderNo,
                                         Integer pageNum, Integer pageSize) {
        if (userId == null) {
            return BaseResponseVO.fail("用户未登录");
        }
        int p = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int s = pageSize == null || pageSize < 1 ? 10 : pageSize;
        List<OrderPO> orders = orderMapper.customerList(userId, status, orderNo, (p - 1) * s, s);
        long total = orderMapper.customerCount(userId, status, orderNo);
        List<OrderGroupVO> groups = toGroups(orders);
        for (OrderGroupVO g : groups) {
            if (g.getShopName() == null || g.getShopName().isEmpty()) {
                g.setShopName(DEFAULT_SHOP_NAME);
            }
        }
        return BaseResponseVO.success(new PageResult<>(groups, total, p, s));
    }

    public BaseResponseVO customerStatusCounts(Integer userId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("ALL", orderMapper.customerStatusCount(userId, null));
        data.put("CREATED", orderMapper.customerStatusCount(userId, "CREATED"));
        data.put("PAID", orderMapper.customerStatusCount(userId, "PAID"));
        data.put("SENT", orderMapper.customerStatusCount(userId, "SENT"));
        data.put("COMPLETED", orderMapper.customerStatusCount(userId, "COMPLETED"));
        return BaseResponseVO.success(data);
    }

    public BaseResponseVO customerDetail(Integer id, Integer userId) {
        OrderPO order = orderMapper.findById(id);
        if (order == null || userId == null || !userId.equals(order.getUserId())) {
            return BaseResponseVO.fail("订单不存在");
        }
        List<OrderGroupVO> groups = toGroups(java.util.Collections.singletonList(order));
        OrderGroupVO vo = groups.get(0);
        vo.setShopName(vo.getShopName() == null || vo.getShopName().isEmpty() ? DEFAULT_SHOP_NAME : vo.getShopName());
        return BaseResponseVO.success(vo);
    }

    /** 删除订单（软删除，仅隐藏） */
    public BaseResponseVO customerDelete(Integer id, Integer userId) {
        int rows = orderMapper.softDelete(id, userId);
        if (rows == 0) {
            return BaseResponseVO.fail("订单不存在或无权删除");
        }
        return BaseResponseVO.success("订单已删除", null);
    }

    /** 立即购买：返回商品信息用于跳转下单 */
    public Map<String, Object> rebuyInfo(Integer orderId) {
        OrderPO order = orderMapper.findById(orderId);
        Map<String, Object> data = new HashMap<>();
        if (order != null) {
            data.put("productId", order.getProductId());
            data.put("productName", order.getProductName());
            data.put("productImage", order.getProductImage());
            data.put("amount", order.getAmount());
            data.put("quantity", order.getQuantity());
        }
        return data;
    }

    // ==================== 转换 ====================

    private List<OrderGroupVO> toGroups(List<OrderPO> orders) {
        if (orders == null || orders.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> ids = orders.stream().map(OrderPO::getId).collect(Collectors.toList());
        List<OrderItemPO> items = orderItemMapper.findByOrderIds(ids);
        Map<Integer, List<OrderItemPO>> itemMap = items.stream()
                .collect(Collectors.groupingBy(OrderItemPO::getOrderId));

        List<OrderGroupVO> groups = new ArrayList<>();
        for (OrderPO o : orders) {
            groups.add(toGroup(o, itemMap.get(o.getId())));
        }
        return groups;
    }

    private OrderGroupVO toGroup(OrderPO o, List<OrderItemPO> items) {
        OrderGroupVO vo = new OrderGroupVO();
        vo.setId(o.getId());
        vo.setOrderNo(o.getOrderNo() != null ? o.getOrderNo() : String.valueOf(o.getId()));
        vo.setCreatedAt(o.getCreatedAt());
        vo.setStoreName(o.getStoreName());
        vo.setStatus(o.getStatus());
        vo.setStatusLabel(o.getStatusLabel());
        vo.setAfterSaleStatus(o.getAfterSaleStatus() == null ? "暂无售后" : o.getAfterSaleStatus());
        vo.setAfterSaleLabel(o.getAfterSaleLabel());
        vo.setPayType(o.getPayType());
        vo.setBuyerName(o.getBuyerName());
        vo.setBuyerNick(o.getBuyerNick());
        vo.setBuyerPhone(maskPhone(o.getBuyerPhone()));
        vo.setBuyerAddress(o.getBuyerAddress());
        vo.setLogisticsType(o.getLogisticsType());
        vo.setDeliveryNo(o.getDeliveryNo());
        vo.setOrderType(o.getOrderType());
        vo.setOrderCategory(o.getOrderCategory());
        vo.setAmount(o.getAmount());
        vo.setProductImage(o.getProductImage());
        vo.setProductName(o.getProductName());
        vo.setPaidAt(o.getPaidAt());
        vo.setShippedAt(o.getShippedAt());
        vo.setCompletedAt(o.getCompletedAt());
        vo.setShopName(DEFAULT_SHOP_NAME);

        List<OrderGroupVO.OrderItemVO> itemVOs = new ArrayList<>();
        int totalQuantity = 0;
        boolean hasGift = false;
        if (items != null && !items.isEmpty()) {
            for (OrderItemPO it : items) {
                OrderGroupVO.OrderItemVO iv = new OrderGroupVO.OrderItemVO();
                iv.setId(it.getId());
                iv.setProductId(it.getProductId());
                iv.setProductName(it.getProductName());
                iv.setProductImage(it.getProductImage());
                iv.setUnitPrice(it.getUnitPrice());
                iv.setQuantity(it.getQuantity());
                iv.setIsGift(it.getIsGift());
                itemVOs.add(iv);
                totalQuantity += it.getQuantity() == null ? 0 : it.getQuantity();
                if (it.getIsGift() != null && it.getIsGift() == 1) {
                    hasGift = true;
                }
            }
        }
        if (itemVOs.isEmpty()) {
            OrderGroupVO.OrderItemVO iv = new OrderGroupVO.OrderItemVO();
            iv.setProductId(o.getProductId());
            iv.setProductName(o.getProductName());
            iv.setProductImage(o.getProductImage());
            int q = o.getQuantity() == null || o.getQuantity() == 0 ? 1 : o.getQuantity();
            iv.setQuantity(q);
            if (o.getAmount() != null) {
                iv.setUnitPrice(o.getAmount().divide(new java.math.BigDecimal(q), 2,
                        java.math.BigDecimal.ROUND_HALF_UP));
            }
            itemVOs.add(iv);
            totalQuantity = q;
        }
        vo.setItems(itemVOs);
        vo.setTotalQuantity(totalQuantity);

        List<String> tags = new ArrayList<>();
        if ("PAID".equals(o.getStatus())) {
            tags.add("待发货");
        }
        if ("SENT".equals(o.getStatus())) {
            tags.add("待收货");
        }
        if ("退款申请中".equals(o.getAfterSaleStatus())) {
            tags.add("退款申请中");
        }
        if ("电子卡券订单".equals(o.getOrderCategory())) {
            tags.add("电子卡券订单");
        }
        if ("同城配送".equals(o.getLogisticsType())) {
            tags.add("同城配送");
        }
        if (hasGift) {
            tags.add("赠品");
        }
        vo.setTags(tags);
        return vo;
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
