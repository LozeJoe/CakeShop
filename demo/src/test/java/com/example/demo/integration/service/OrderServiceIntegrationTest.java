package com.example.demo.integration.service;

import com.javaBean.User;
import com.service.OrderService;
import com.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("OrderService 集成测试")
class OrderServiceIntegrationTest {

    @Autowired private OrderService orderService;
    @Autowired private UserService userService;

    @Test @DisplayName("查询用户订单分页")
    void getOrdersByUserIdPage() {
        User user = userService.getUserByName("vili");
        assertNotNull(user);
        var result = orderService.getOrdersByUserIdPage(user.getId(), 1, 10);
        assertNotNull(result);
    }

    @Test @DisplayName("查询订单详情")
    void getOrderById() {
        var order = orderService.getOrderById("20260601001");
        if (order != null) {
            assertNotNull(order.getId());
        }
    }

    @Test @DisplayName("获取订单状态分布")
    void getOrderStatusDistribution() {
        var distribution = orderService.getOrderStatusDistribution();
        assertNotNull(distribution);
    }

    @Test @DisplayName("过滤订单分页")
    void getFilteredOrdersPage() {
        var result = orderService.getFilteredOrdersPage(0, null, 1, 10);
        assertNotNull(result);
    }
}
