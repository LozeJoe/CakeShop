package com.example.demo.integration.service;

import com.javaBean.User;
import com.service.RiderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("RiderService 集成测试")
class RiderServiceIntegrationTest {

    @Autowired
    private RiderService riderService;

    @Test
    @DisplayName("骑手登录成功")
    void riderLoginSuccess() {
        User rider = riderService.login("rider1", "123");
        assertNotNull(rider);
        assertEquals("rider1", rider.getUsername());
        assertEquals("2", rider.getIsadmin());
    }

    @Test
    @DisplayName("骑手登录失败 - 非骑手用户")
    void riderLoginFailNotRider() {
        User rider = riderService.login("admin", "admin");
        assertNull(rider);
    }

    @Test
    @DisplayName("获取待接单列表")
    void getPendingOrders() {
        var result = riderService.getPendingOrders(1, 10);
        assertNotNull(result);
        assertNotNull(result.getData());
    }
}
