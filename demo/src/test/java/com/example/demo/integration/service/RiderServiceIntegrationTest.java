package com.example.demo.integration.service;

import com.javaBean.User;
import com.mapper.UserMapper;
import com.service.RiderService;
import com.service.UserService;
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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("骑手登录成功")
    void riderLoginSuccess() {
        // Debug: check rider1 exists
        User dbUser = userMapper.getUserByName("rider1");
        assertNotNull(dbUser, "rider1 应在数据库中存在");
        assertEquals("2", dbUser.getIsadmin(), "rider1 应为骑手角色");
        assertTrue(dbUser.getPassword().startsWith("$2a$"), "密码应为 BCrypt 格式，实际: " + dbUser.getPassword().substring(0, Math.min(10, dbUser.getPassword().length())));

        // Debug: check BCrypt matches directly
        boolean bcryptMatch = userService.matchesPassword("Rider1234!", dbUser.getPassword());
        assertTrue(bcryptMatch, "BCrypt 密码验证应成功");

        // Full login test
        User rider = riderService.login("rider1", "Rider1234!");
        assertNotNull(rider, "riderService.login('rider1', 'Rider1234!') 应返回非空");
        assertEquals("rider1", rider.getUsername());
        assertEquals("2", rider.getIsadmin());
    }

    @Test
    @DisplayName("骑手登录失败 - 非骑手用户")
    void riderLoginFailNotRider() {
        User rider = riderService.login("admin", "admin123");
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