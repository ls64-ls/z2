package com.zsl.b2cmall.shop.web;

import com.zsl.b2cmall.common.response.BaseResponseVO;
import com.zsl.b2cmall.shop.service.OrderManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * C 端我的订单
 */
@RestController
@RequestMapping("/c/order")
public class CustomerOrderController {

    @Autowired
    private OrderManageService orderManageService;

    /** 订单列表（状态 Tab / 订单编号精确查询） */
    @GetMapping("/list")
    public BaseResponseVO list(@RequestParam Integer userId,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) String orderNo,
                               @RequestParam(required = false) Integer pageNum,
                               @RequestParam(required = false) Integer pageSize) {
        return orderManageService.customerOrders(userId, status, orderNo, pageNum, pageSize);
    }

    /** 各状态 Tab 数量 */
    @GetMapping("/status-count")
    public BaseResponseVO statusCount(@RequestParam Integer userId) {
        return orderManageService.customerStatusCounts(userId);
    }

    @GetMapping("/detail")
    public BaseResponseVO detail(@RequestParam Integer id, @RequestParam Integer userId) {
        return orderManageService.customerDetail(id, userId);
    }

    /** 删除订单（软删除） */
    @PostMapping("/delete")
    public BaseResponseVO delete(@RequestBody Map<String, Object> body) {
        Integer id = Integer.valueOf(String.valueOf(body.get("id")));
        Integer userId = Integer.valueOf(String.valueOf(body.get("userId")));
        return orderManageService.customerDelete(id, userId);
    }

    /** 立即购买：获取商品信息 */
    @GetMapping("/rebuy")
    public BaseResponseVO rebuy(@RequestParam Integer orderId) {
        return BaseResponseVO.success(orderManageService.rebuyInfo(orderId));
    }
}
