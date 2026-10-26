package com.zsl.b2cmall.shop.web;

import com.zsl.b2cmall.common.response.BaseResponseVO;
import com.zsl.b2cmall.shop.request.OrderQueryVO;
import com.zsl.b2cmall.shop.request.ShipRequestVO;
import com.zsl.b2cmall.shop.service.OrderManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * B 端订单查询与管理
 */
@RestController
@RequestMapping("/order")
public class OrderManageController {

    @Autowired
    private OrderManageService orderManageService;

    /** 多条件分页查询 */
    @GetMapping("/page")
    public BaseResponseVO page(OrderQueryVO query) {
        return orderManageService.page(query);
    }

    @GetMapping("/detail")
    public BaseResponseVO detail(@RequestParam Integer id) {
        return orderManageService.detail(id);
    }

    /** 状态快捷标签栏计数 */
    @GetMapping("/status-count")
    public BaseResponseVO statusCount(@RequestParam Integer shopId) {
        return orderManageService.statusCounts(shopId);
    }

    /** 单条 / 批量发货 */
    @PostMapping("/ship")
    public BaseResponseVO ship(@RequestBody ShipRequestVO request) {
        return orderManageService.ship(request);
    }

    /** 导出当前筛选订单 */
    @GetMapping("/export")
    public void export(OrderQueryVO query, HttpServletResponse response) {
        orderManageService.export(query, response);
    }

    /** 退款信息 */
    @GetMapping("/refund")
    public BaseResponseVO refund(@RequestParam Integer orderId) {
        return orderManageService.refund(orderId);
    }
}
