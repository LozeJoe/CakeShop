package com.example.demo.controller;

import com.example.demo.config.TestBeans;
import com.javaBean.User;
import com.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("CartController 测试")
class CartControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private CartService cartService;
    @MockBean private GoodsService goodsService;

    private final User testUser = TestBeans.createTestUser();

    @Test @DisplayName("未登录跳转")
    void cartListRequiresLogin() throws Exception {
        mockMvc.perform(get("/cart/cartList"))
                .andExpect(status().is3xxRedirection());
    }

    @Test @DisplayName("已登录显示购物车")
    void cartListLoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);
        when(cartService.getCartByUserName("testuser")).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/cart/cartList").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("cartList"));
    }

    @Test @DisplayName("清空购物车")
    void clearCart() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);
        when(cartService.clearCart("testuser")).thenReturn(1);
        mockMvc.perform(get("/cart/clearCart").session(session))
                .andExpect(status().is3xxRedirection());
    }

    @Test @DisplayName("删除购物车项")
    void deleteCart() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);
        when(cartService.deleteCart(1)).thenReturn(1);
        mockMvc.perform(get("/cart/deleteCart").param("cartId", "1").session(session))
                .andExpect(status().is3xxRedirection());
    }
}
