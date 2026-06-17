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

    @Test @DisplayName("登录成功")
    void loginSuccess() {
        when(userMapper.login("testuser", "202cb962ac59075b964b07152d234b70")).thenReturn(testUser);
        User result = userService.login("testuser", "123");
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test @DisplayName("登录失败 - 错误密码")
    void loginFailWrongPassword() {
        when(userMapper.login(anyString(), anyString())).thenReturn(null);
        assertNull(userService.login("testuser", "wrong"));
    }

    @Test @DisplayName("登录失败 - 空用户名")
    void loginFailEmptyUsername() {
        // Service 不会校验空用户名，直接传给 Mapper
        assertNull(userService.login("  ", "123"));
    }

    @Test @DisplayName("登录失败 - 空密码")
    void loginFailEmptyPassword() {
        assertNull(userService.login("testuser", "  "));
    }

    @Test @DisplayName("MD5 加密验证")
    void md5Hash() {
        assertEquals("202cb962ac59075b964b07152d234b70", userService.md5("123"));
    }

    @Test @DisplayName("MD5 空字符串")
    void md5Empty() {
        String hash = userService.md5("");
        assertNotNull(hash);
        assertEquals(32, hash.length());
    }

    @Test @DisplayName("注册用户")
    void registerUser() {
        userService.addUser(testUser);
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
