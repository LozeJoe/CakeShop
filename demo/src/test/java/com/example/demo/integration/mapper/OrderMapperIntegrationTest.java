package com.example.demo.integration.mapper;

import com.javaBean.Order;
import com.javaBean.OrderItem;
import com.mapper.OrderMapper;
import com.mapper.OrderItemMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("OrderMapper 集成测试")
class OrderMapperIntegrationTest {

    @Autowired private OrderMapper orderMapper;
    @Autowired private OrderItemMapper orderItemMapper;

    @Test @DisplayName("按ID查询订单")
    void getOrderById() {
        List<Order> allOrders = orderMapper.getOrdersByPage(0, 1);
        if (!allOrders.isEmpty()) {
            Order order = orderMapper.getOrderById(allOrders.get(0).getId());
            assertNotNull(order);
        }
    }

    @Test @DisplayName("获取过滤订单列表")
    void getFilteredOrders() {
        List<Order> orders = orderMapper.getFilteredOrders(0, null, 0, 10);
        assertNotNull(orders);
    }

    @Test @DisplayName("待接单订单")
    void getPendingOrders() {
        List<Order> pending = orderMapper.getPendingOrders(0, 10);
        assertNotNull(pending);
    }

    @Test @DisplayName("订单项查询")
    void getOrderItems() {
        List<Order> allOrders = orderMapper.getOrdersByPage(0, 1);
        if (!allOrders.isEmpty()) {
            List<OrderItem> items = orderItemMapper.getOrderItemsByOrderId(allOrders.get(0).getId());
            assertNotNull(items);
        }
    }

    @Test @DisplayName("订单总数")
    void getOrderCount() {
        assertTrue(orderMapper.getOrderCount() > 0);
    }
}
