package com.service;

import com.javaBean.Cart;
import com.mapper.CartMapper;
import javax.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    @Resource
    private CartMapper cartMapper;

    @Override
    public List<Cart> getCartByUserName(String userName) {
        return cartMapper.getCartByUserName(userName);
    }

    @Override
    public Cart getCartByUserNameAndGoodId(String userName, String goodId) {
        return cartMapper.getCartByUserNameAndGoodId(userName, goodId);
    }

    @Override
    public int getCartCount(String userName) {
        return cartMapper.getCartCount(userName);
    }

    @Override
    public int addCart(Cart cart) {
        return cartMapper.addCart(cart);
    }

    @Override
    public int updateCart(Cart cart) {
        return cartMapper.updateCart(cart);
    }

    @Override
    public int deleteCart(int id) {
        return cartMapper.deleteCart(id);
    }

    @Override
    public int clearCart(String userName) {
        return cartMapper.clearCart(userName);
    }
}