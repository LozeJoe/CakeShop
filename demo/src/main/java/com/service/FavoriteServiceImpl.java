package com.service;

import com.javaBean.Favorite;
import com.mapper.FavoriteMapper;
import javax.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;


/**
 * 收藏服务实现类，提供收藏的增删查等业务逻辑实现。
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Resource
    private FavoriteMapper favoriteMapper;

    @Override
    public List<Favorite> getFavoritesByUserId(int userId) {
        return favoriteMapper.getFavoritesByUserId(userId);
    }

    @Override
    public Favorite getFavorite(int userId, int goodsId) {
        return favoriteMapper.getFavorite(userId, goodsId);
    }

    @Override
    public int getFavoriteCount(int userId) {
        return favoriteMapper.getFavoriteCount(userId);
    }

    /**
     * 新增数据。
     */
    @Override
    public void addFavorite(Favorite favorite) {
        favoriteMapper.addFavorite(favorite);
    }

    /**
     * 删除数据。
     */
    @Override
    public void deleteFavorite(int id) {
        favoriteMapper.deleteFavorite(id);
    }

    /**
     * 删除数据。
     */
    @Override
    public void removeFavorite(int userId, int goodsId) {
        favoriteMapper.removeFavorite(userId, goodsId);
    }

    @Override
    public boolean isFavorited(int userId, int goodsId) {
        return favoriteMapper.getFavorite(userId, goodsId) != null;
    }
}
