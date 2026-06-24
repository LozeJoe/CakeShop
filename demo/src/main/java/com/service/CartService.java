package com.service;

import com.javaBean.Cart;
import java.util.List;


/**
 * 购物车服务接口，定义购物车增删改查等业务方法。
 */
public interface CartService {
    List<Cart> getCartByUserName(String userName);
    Cart getCartByUserNameAndGoodId(String userName, String goodId);
    int getCartCount(String userName);
    int addCart(Cart cart);
    int updateCart(Cart cart);
    int deleteCart(int id);
    int clearCart(String userName);
}