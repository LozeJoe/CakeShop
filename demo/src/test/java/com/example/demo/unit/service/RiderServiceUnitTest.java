package com.example.demo.unit.service;

import com.example.demo.config.TestBeans;
import com.javaBean.Order;
import com.javaBean.PageResult;
import com.javaBean.User;
import com.mapper.OrderMapper;
import com.mapper.UserMapper;
import com.service.OrderService;
import com.service.RiderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RiderService 单元测试")
class RiderServiceUnitTest {

    @Mock private UserMapper userMapper;
    @Mock private OrderMapper orderMapper;
    @Mock private OrderService orderService;
    @InjectMocks private RiderService riderService;
    private User testRider;

    @BeforeEach
    void setUp() {
        testRider = TestBeans.createTestRider();
    }

    @Test @DisplayName("骑手登录成功")
    void loginSuccess() {
        when(userMapper.login("rider1", "123")).thenReturn(testRider);
        User result = riderService.login("rider1", "123");
        assertNotNull(result);
        assertEquals("rider1", result.getUsername());
        assertEquals("2", result.getIsadmin());
    }

    @Test @DisplayName("骑手登录失败 - 非骑手用户")
    void loginFailNotRider() {
        when(userMapper.login("testuser", "123")).thenReturn(TestBeans.createTestUser());
        assertNull(riderService.login("testuser", "123"));
    }

    @Test @DisplayName("骑手登录失败 - 不存在")
    void loginFailNotFound() {
        when(userMapper.login("nobody", "123")).thenReturn(null);
        assertNull(riderService.login("nobody", "123"));
    }

    @Test @DisplayName("骑手接单")
    void acceptOrder() {
        doNothing().when(orderService).acceptOrder("ORDER001", 3);
        riderService.acceptOrder("ORDER001", 3);
        verify(orderService).acceptOrder("ORDER001", 3);
    }

    @Test @DisplayName("骑手开始配送")
    void startDelivery() {
        doNothing().when(orderService).startDelivery("ORDER001", 3);
        riderService.startDelivery("ORDER001", 3);
        verify(orderService).startDelivery("ORDER001", 3);
    }

    @Test @DisplayName("骑手完成配送")
    void completeDelivery() {
        doNothing().when(orderService).completeDelivery("ORDER001", 3, 20.0);
        riderService.completeDelivery("ORDER001", 3, 20.0);
        verify(orderService).completeDelivery("ORDER001", 3, 20.0);
        verify(userMapper).addBalance(3, 20.0);
    }

    @Test @DisplayName("获取待接单列表")
    void getPendingOrders() {
        when(orderMapper.getPendingOrders(0, 10)).thenReturn(Collections.singletonList(TestBeans.createTestOrder()));
        when(orderMapper.getPendingCount()).thenReturn(1);
        PageResult<Order> result = riderService.getPendingOrders(1, 10);
        assertEquals(1, result.getData().size());
    }
}
