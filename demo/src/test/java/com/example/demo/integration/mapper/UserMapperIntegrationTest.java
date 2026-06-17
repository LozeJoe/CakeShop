package com.example.demo.integration.mapper;

import com.javaBean.User;
import com.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("UserMapper 集成测试")
class UserMapperIntegrationTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("查询所有非骑手用户")
    void getAllUser() {
        List<User> users = userMapper.getAllUser();
        assertNotNull(users);
        // Should contain admin + testuser (but not riders with isadmin='2')
        assertTrue(users.size() >= 2);
    }

    @Test
    @DisplayName("根据用户名和密码登录")
    void login() {
        // admin/21232f297a57a5a743894a0e4a801fc3 = admin/admin 的MD5
        User user = userMapper.login("admin", "21232f297a57a5a743894a0e4a801fc3");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
        assertEquals("1", user.getIsadmin());
    }

    @Test
    @DisplayName("登录失败 - 错误密码")
    void loginFail() {
        User user = userMapper.login("admin", "wrongpassword");
        assertNull(user);
    }

    @Test
    @DisplayName("根据用户名查询")
    void getUserByName() {
        User user = userMapper.getUserByName("admin");
        assertNotNull(user);
        assertEquals("admin", user.getUsername());
    }

    @Test
    @DisplayName("根据ID查询用户")
    void getUserById() {
        User user = userMapper.getUserById(1);
        if (user != null) {
            assertNotNull(user.getUsername());
        }
    }

    @Test
    @DisplayName("分页查询")
    void getUserByPage() {
        List<User> users = userMapper.getUserByPage(0, 5);
        assertNotNull(users);
        assertTrue(users.size() <= 5);
    }

    @Test
    @DisplayName("获取用户总数")
    void getUserCount() {
        int count = userMapper.getUserCount();
        assertTrue(count > 0);
    }

    @Test
    @DisplayName("获取未审核用户")
    void getUnverifiedUsers() {
        List<User> users = userMapper.getUnverifiedUsers(0, 10);
        assertNotNull(users);
    }

    @Test
    @DisplayName("获取所有骑手")
    void getAllRiders() {
        List<User> riders = userMapper.getAllRiders();
        assertNotNull(riders);
    }
}
