package com.example.demo.integration.service;

import com.javaBean.User;
import com.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("UserService 集成测试")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("管理员登录成功")
    void adminLoginSuccess() {
        User user = userService.login("admin", "admin123");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertEquals("1", user.getIsadmin());
    }

    @Test
    @DisplayName("普通用户登录成功")
    void userLoginSuccess() {
        User user = userService.login("vili", "Vili1234!");
        assertNotNull(user);
        assertEquals("vili", user.getUsername());
        assertEquals("0", user.getIsadmin());
    }

    @Test
    @DisplayName("登录失败 - 错误密码")
    void loginFailWrongPassword() {
        User user = userService.login("admin", "wrongpassword");
        assertNull(user);
    }

    @Test
    @DisplayName("按用户名查询")
    void getUserByName() {
        User user = userService.getUserByName("admin");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }

    @Test
    @DisplayName("分页查询用户")
    void getUserByPage() {
        var result = userService.getUserByPage(1, 5);
        assertNotNull(result);
        assertTrue(result.getData().size() <= 5);
        assertTrue(result.getTotalCount() > 0);
    }

    @Test
    @DisplayName("获取未审核用户")
    void getUnverifiedUsers() {
        var result = userService.getUnverifiedUsers(1, 10);
        assertNotNull(result);
    }
}
