package com.service;

import com.javaBean.Favorite;
import com.mapper.FavoriteMapper;
import javax.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

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

    @Override
    public void addFavorite(Favorite favorite) {
        favoriteMapper.addFavorite(favorite);
    }

    @Override
    public void deleteFavorite(int id) {
        favoriteMapper.deleteFavorite(id);
    }

    @Override
    public void removeFavorite(int userId, int goodsId) {
        favoriteMapper.removeFavorite(userId, goodsId);
    }

    @Override
    public boolean isFavorited(int userId, int goodsId) {
        return favoriteMapper.getFavorite(userId, goodsId) != null;
    }
}
