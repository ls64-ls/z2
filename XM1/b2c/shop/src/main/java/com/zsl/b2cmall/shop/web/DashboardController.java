package com.zsl.b2cmall.shop.web;

import com.zsl.b2cmall.common.response.BaseResponseVO;
import com.zsl.b2cmall.shop.request.DashboardVO;
import com.zsl.b2cmall.shop.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Dashboard 数据：消息通知 + 订单统计
     */
    @GetMapping
    public BaseResponseVO<DashboardVO> getDashboard(@RequestParam Integer shopId) {
        DashboardVO data = dashboardService.getDashboardData(shopId);
        return BaseResponseVO.success(data);
    }
}
