package com.service;

import com.javaBean.Cart;
import java.util.List;

public interface CartService {
    List<Cart> getCartByUserName(String userName);
    Cart getCartByUserNameAndGoodId(String userName, String goodId);
    int getCartCount(String userName);
    int addCart(Cart cart);
    int updateCart(Cart cart);
    int deleteCart(int id);
    int clearCart(String userName);
}