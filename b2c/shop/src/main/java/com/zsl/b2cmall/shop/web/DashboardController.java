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
     * Dashboard 数据：6 个状态卡片 + 24 小时交易趋势 + 4 项经营指标 + 消息通知
     */
    @GetMapping
    public BaseResponseVO<DashboardVO> getDashboard(@RequestParam Integer shopId) {
        DashboardVO data = dashboardService.getDashboardData(shopId);
        return BaseResponseVO.success(data);
    }

    /**
     * 标记消息已读
     */
    @PostMapping("/message/read")
    public BaseResponseVO readMessage(@RequestParam Integer id) {
        dashboardService.markRead(id);
        return BaseResponseVO.success();
    }
}
