package com.example.demo.unit.service;

import com.javaBean.Cart;
import com.mapper.CartMapper;
import com.service.CartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService 单元测试")
class CartServiceUnitTest {

    @Mock private CartMapper cartMapper;
    @InjectMocks private CartServiceImpl cartService;

    private Cart makeCart(int id, String goodId, String userName, int amount, double price) {
        Cart c = new Cart();
        c.setId(id);
        c.setGoodId(goodId);
        c.setUserName(userName);
        c.setAmount(amount);
        c.setPrice(price);
        c.setTotalPrice(price * amount);
        return c;
    }

    @Test @DisplayName("获取用户购物车列表")
    void getCartByUsername() {
        when(cartMapper.getCartByUserName("testuser")).thenReturn(Arrays.asList(makeCart(1, "1", "testuser", 2, 100)));
        assertEquals(1, cartService.getCartByUserName("testuser").size());
    }

    @Test @DisplayName("根据用户名和商品ID查询")
    void getCartByUserNameAndGoodId() {
        Cart c = makeCart(1, "1", "testuser", 2, 100);
        when(cartMapper.getCartByUserNameAndGoodId("testuser", "1")).thenReturn(c);
        assertNotNull(cartService.getCartByUserNameAndGoodId("testuser", "1"));
    }

    @Test @DisplayName("添加商品到购物车")
    void addCart() {
        Cart c = makeCart(1, "1", "testuser", 1, 100);
        when(cartMapper.addCart(c)).thenReturn(1);
        assertEquals(1, cartService.addCart(c));
        verify(cartMapper).addCart(c);
    }

    @Test @DisplayName("更新购物车数量")
    void updateCart() {
        Cart c = makeCart(1, "1", "testuser", 3, 100);
        when(cartMapper.updateCart(c)).thenReturn(1);
        assertEquals(1, cartService.updateCart(c));
    }

    @Test @DisplayName("删除购物车项")
    void deleteCart() {
        when(cartMapper.deleteCart(1)).thenReturn(1);
        assertEquals(1, cartService.deleteCart(1));
    }

    @Test @DisplayName("清空购物车")
    void clearCart() {
        when(cartMapper.clearCart("testuser")).thenReturn(2);
        assertEquals(2, cartService.clearCart("testuser"));
    }

    @Test @DisplayName("获取购物车数量")
    void getCartCount() {
        when(cartMapper.getCartCount("testuser")).thenReturn(3);
        assertEquals(3, cartService.getCartCount("testuser"));
    }
}
