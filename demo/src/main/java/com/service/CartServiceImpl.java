package com.service;

import com.javaBean.Cart;
import com.mapper.CartMapper;
import javax.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;


/**
 * 购物车服务实现类，提供购物车增删改查等业务逻辑实现。
 */
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

    /**
     * 新增数据。
     */
    @Override
    public int addCart(Cart cart) {
        return cartMapper.addCart(cart);
    }

    /**
     * 更新数据。
     */
    @Override
    public int updateCart(Cart cart) {
        return cartMapper.updateCart(cart);
    }

    /**
     * 删除数据。
     */
    @Override
    public int deleteCart(int id) {
        return cartMapper.deleteCart(id);
    }

    /**
     * 清空购物车。
     */
    @Override
    public int clearCart(String userName) {
        return cartMapper.clearCart(userName);
    }
}