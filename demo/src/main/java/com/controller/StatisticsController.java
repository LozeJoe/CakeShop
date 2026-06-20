package com.controller;

import com.service.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 管理后台统计 API — 所有端点返回 JSON，
 * 供 admin/index.html 通过 AJAX 局部刷新使用。
 */
@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {

    @Resource private UserService userService;
    @Resource private OrderService orderService;
    @Resource private GoodsService goodsService;

    /**
     * 聚合所有管理后台统计数据，一次请求返回全部。
     */
    @RequestMapping("/all")
    public Map<String, Object> all(@RequestParam(value = "threshold", defaultValue = "5") int threshold) {
        Map<String, Object> result = new LinkedHashMap<>();

        // ── KPI 指标 ──
        result.put("totalRevenue",     orderService.getTotalRevenue());
        result.put("todayRevenue",     orderService.getTodayRevenue());
        result.put("todayOrders",      orderService.getTodayOrderCount());
        result.put("totalOrders",      orderService.getOrderCount());
        result.put("completedOrders",  orderService.getCompletedOrderCount());
        result.put("pendingOrders",    orderService.getPendingOrderCount());
        result.put("totalUsers",       userService.getUserCount());
        result.put("riderCount",       userService.getRiderCount());
        result.put("totalGoods",       goodsService.getGoodsCount());
        result.put("totalStock",       goodsService.getTotalStock());
        result.put("avgOrderValue",    Math.round(orderService.getAvgOrderValue() * 100.0) / 100.0);
        result.put("lowStockCount",    goodsService.getLowStockCount(threshold));

        // ── 图表数据 ──
        java.time.LocalDate today = java.time.LocalDate.now();
        String monthStart = today.minusMonths(11).toString();   // e.g. 2025-07-20
        String weekStart  = today.minusDays(6).toString();
        String month30Start = today.minusDays(29).toString();

        result.put("monthlySales",         orderService.getMonthlySales(monthStart));
        result.put("orderStatusDist",      orderService.getOrderStatusDistribution());
        result.put("topGoods",             orderService.getTopGoods());
        result.put("weeklyOrders",         orderService.getWeeklyOrders(weekStart));
        result.put("revenueLast30Days",    orderService.getRevenueLast30Days(month30Start));
        result.put("categorySales",        orderService.getCategorySales());

        return result;
    }

    /**
     * 库存预警 — 返回低库存商品列表，供弹窗展示。
     */
    @RequestMapping("/lowStock")
    public Map<String, Object> lowStock(@RequestParam(value = "threshold", defaultValue = "5") int threshold) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("count", goodsService.getLowStockCount(threshold));
        result.put("goods", goodsService.getLowStockGoods(threshold));
        return result;
    }
}
