package com.service;

import com.javaBean.Goods;
import com.javaBean.PageResult;
import com.mapper.GoodsMapper;
import javax.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;


/**
 * 商品服务实现类，提供商品增删改查等业务逻辑实现。
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsMapper goodsMapper;

    /**
     * 获取所有商品列表。
     */
    @Override
    public List<Goods> getAllGoods() {
        return goodsMapper.getAllGoods();
    }

    /**
     * 分页获取商品列表。
     */
    @Override
    public PageResult<Goods> getGoodsByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Goods> data = goodsMapper.getGoodsByPage(offset, pageSize);
        int totalCount = goodsMapper.getGoodsCount();
        return new PageResult<>(data, pageNum, pageSize, totalCount);
    }

    /**
     * 查询获取数据。
     */
    @Override
    public PageResult<Goods> getGoodsByTypePage(int typeId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Goods> data = goodsMapper.getGoodsByTypePage(typeId, offset, pageSize);
        int totalCount = goodsMapper.getGoodsCountByType(typeId);
        return new PageResult<>(data, pageNum, pageSize, totalCount);
    }

    /**
     * 查询数据。
     */
    @Override
    public PageResult<Goods> searchGoodsPage(String keyword, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Goods> data = goodsMapper.searchGoodsPage(keyword, offset, pageSize);
        int totalCount = goodsMapper.searchGoodsCount(keyword);
        return new PageResult<>(data, pageNum, pageSize, totalCount);
    }

    /**
     * 根据分类获取商品。
     */
    @Override
    public List<Goods> getGoodsByType(int typeId) {
        return goodsMapper.getGoodsByType(typeId);
    }

    /**
     * 根据ID查询商品。
     */
    @Override
    public Goods getGoodsById(int id) {
        return goodsMapper.getGoodsById(id);
    }

    /**
     * 查询数据。
     */
    @Override
    public List<Goods> searchGoods(String keyword) {
        return goodsMapper.searchGoods(keyword);
    }

    @Override
    public List<Goods> getTopSellGoods() {
        return goodsMapper.getTopSellGoods();
    }

    @Override
    public List<Goods> getNewGoods() {
        return goodsMapper.getNewGoods();
    }

    @Override
    public PageResult<Goods> getTopSellGoodsPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Goods> data = goodsMapper.getTopSellGoodsPage(offset, pageSize);
        int totalCount = goodsMapper.getGoodsCount();
        return new PageResult<>(data, pageNum, pageSize, totalCount);
    }

    @Override
    public PageResult<Goods> getNewGoodsPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Goods> data = goodsMapper.getNewGoodsPage(offset, pageSize);
        int totalCount = goodsMapper.getGoodsCount();
        return new PageResult<>(data, pageNum, pageSize, totalCount);
    }

    /**
     * 执行对应业务操作。
     */
    @Override
    public void decreaseStock(int id, int count) {
        goodsMapper.decreaseStock(id, count);
    }

    /**
     * 执行对应业务操作。
     */
    @Override
    public void increaseSales(int id, int count) {
        goodsMapper.increaseSales(id, count);
    }

    /**
     * 查询获取数据。
     */
    @Override
    public int getGoodsCount() {
        return goodsMapper.getGoodsCount();
    }

    /**
     * 新增数据。
     */
    @Override
    public void addGoods(Goods goods) {
        goodsMapper.addGoods(goods);
    }

    /**
     * 更新数据。
     */
    @Override
    public void updateGoods(Goods goods) {
        goodsMapper.updateGoods(goods);
    }

    /**
     * 删除数据。
     */
    @Override
    public void deleteGoods(int id) {
        goodsMapper.deleteGoods(id);
    }

    @Override
    public int getLowStockCount(int threshold) {
        return goodsMapper.getLowStockCount(threshold);
    }

    @Override
    public int getTotalStock() { return goodsMapper.getTotalStock(); }

    @Override
    public List<Goods> getLowStockGoods(int threshold) { return goodsMapper.getLowStockGoods(threshold); }

    /**
     * 更新数据。
     */
    @Override
    public void updateGoodsStatus(int id, int status) { goodsMapper.updateGoodsStatus(id, status); }

    /**
     * 管理员分页获取商品列表。
     */
    @Override
    public PageResult<Goods> getGoodsByPageAdmin(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Goods> data = goodsMapper.getGoodsByPageAdmin(offset, pageSize);
        int totalCount = goodsMapper.getGoodsCountAdmin();
        return new PageResult<>(data, pageNum, pageSize, totalCount);
    }
}