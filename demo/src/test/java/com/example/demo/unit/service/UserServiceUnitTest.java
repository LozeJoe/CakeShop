package com.example.demo.unit.service;

import com.example.demo.config.TestBeans;
import com.javaBean.User;
import com.mapper.UserMapper;
import com.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 单元测试")
class UserServiceUnitTest {

    @Mock private UserMapper userMapper;
    @InjectMocks private UserServiceImpl userService;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = TestBeans.createTestUser();
    }

    @Test @DisplayName("BCrypt 加密-验证-匹配")
    void bcryptEncodeAndMatch() {
        String rawPw = "Test1234!";
        String encoded = userService.encodePassword(rawPw);
        assertNotNull(encoded);
        assertTrue(encoded.startsWith("$2a$"), "BCrypt hash 应以 $2a$ 开头");
        assertTrue(userService.matchesPassword(rawPw, encoded), "明文应与 BCrypt hash 匹配");
        assertFalse(userService.matchesPassword("WrongPw1!", encoded), "错误密码不应匹配");
    }

    @Test @DisplayName("登录成功 - BCrypt")
    void loginSuccess() {
        String rawPw = "Test1234!";
        String encoded = userService.encodePassword(rawPw);
        testUser.setPassword(encoded);

        when(userMapper.getUserByName("testuser")).thenReturn(testUser);

        User result = userService.login("testuser", rawPw);
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test @DisplayName("登录失败 - 错误密码")
    void loginFailWrongPassword() {
        String encoded = userService.encodePassword("RealPw1!");
        testUser.setPassword(encoded);

        when(userMapper.getUserByName("testuser")).thenReturn(testUser);

        assertNull(userService.login("testuser", "WrongPw1!"));
    }

    @Test @DisplayName("登录失败 - 用户不存在")
    void loginFailUserNotFound() {
        when(userMapper.getUserByName("nonexistent")).thenReturn(null);
        assertNull(userService.login("nonexistent", "Test1234!"));
    }

    @Test @DisplayName("注册用户 - BCrypt加密")
    void registerUser() {
        String rawPw = "Test1234!";
        userService.register("newuser", rawPw, "测试", "13800138000", "北京市", "test@test.com", "0");
        verify(userMapper).register(eq("newuser"), startsWith("$2a$"), eq("测试"), eq("13800138000"), eq("北京市"), eq("test@test.com"), eq("0"));
    }

    @Test @DisplayName("addUser - BCrypt加密")
    void addUser() {
        String rawPw = "Test1234!";
        testUser.setPassword(rawPw);
        userService.addUser(testUser);
        // 验证密码已被 BCrypt 加密
        assertTrue(testUser.getPassword().startsWith("$2a$"), "密码应被 BCrypt 加密");
        verify(userMapper).addUser(testUser);
    }

    @Test @DisplayName("分页查询用户")
    void getUserByPage() {
        when(userMapper.getUserByPage(0, 10)).thenReturn(Arrays.asList(testUser));
        when(userMapper.getUserCount()).thenReturn(1);
        var result = userService.getUserByPage(1, 10);
        assertEquals(1, result.getData().size());
    }

    @Test @DisplayName("按用户名查询")
    void getUserByName() {
        when(userMapper.getUserByName("testuser")).thenReturn(testUser);
        assertNotNull(userService.getUserByName("testuser"));
    }

    @Test @DisplayName("冻结/解冻用户")
    void freezeAndUnfreezeUser() {
        userService.freezeUser(1); verify(userMapper).freezeUser(1);
        userService.unfreezeUser(1); verify(userMapper).unfreezeUser(1);
    }

    @Test @DisplayName("验证用户")
    void verifyUser() { userService.verifyUser(1); verify(userMapper).verifyUser(1); }

    @Test @DisplayName("设置管理员权限")
    void setUserAdmin() { userService.setUserAdmin(1, "1"); verify(userMapper).setUserAdmin(1, "1"); }

    @Test @DisplayName("更新用户信息")
    void updateUser() {
        userService.updateUser(testUser);
        verify(userMapper).updateUser(testUser);
    }
}