package com.service;

import com.javaBean.Goods;
import com.javaBean.PageResult;
import java.util.List;


/**
 * 商品服务接口，定义商品查询、添加、更新、删除等业务方法。
 */
public interface GoodsService {
    List<Goods> getAllGoods();
    PageResult<Goods> getGoodsByPage(int pageNum, int pageSize);
    PageResult<Goods> getGoodsByTypePage(int typeId, int pageNum, int pageSize);
    PageResult<Goods> searchGoodsPage(String keyword, int pageNum, int pageSize);
    List<Goods> getGoodsByType(int typeId);
    Goods getGoodsById(int id);
    List<Goods> searchGoods(String keyword);
    List<Goods> getTopSellGoods();
    List<Goods> getNewGoods();
    PageResult<Goods> getTopSellGoodsPage(int pageNum, int pageSize);
    PageResult<Goods> getNewGoodsPage(int pageNum, int pageSize);
    void decreaseStock(int id, int count);
    void increaseSales(int id, int count);
    int getGoodsCount();
    void addGoods(Goods goods);
    void updateGoods(Goods goods);
    void deleteGoods(int id);
    int getLowStockCount(int threshold);
    int getTotalStock();
    List<Goods> getLowStockGoods(int threshold);
    void updateGoodsStatus(int id, int status);
    PageResult<Goods> getGoodsByPageAdmin(int pageNum, int pageSize);
}