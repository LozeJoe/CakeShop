package com.service;

import com.javaBean.Favorite;
import java.util.List;


/**
 * 收藏服务接口，定义收藏相关业务方法。
 */
public interface FavoriteService {
    List<Favorite> getFavoritesByUserId(int userId);
    Favorite getFavorite(int userId, int goodsId);
    int getFavoriteCount(int userId);
    void addFavorite(Favorite favorite);
    void deleteFavorite(int id);
    void removeFavorite(int userId, int goodsId);
    boolean isFavorited(int userId, int goodsId);
}
