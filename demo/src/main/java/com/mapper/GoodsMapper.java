package com.mapper;

import com.javaBean.Goods;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GoodsMapper {
    @Select("select g.*, t.name as typeName from goods g left join type t on g.type_id = t.id where g.status = 1")
    public List<Goods> getAllGoods();
    
    @Select("select g.*, t.name as typeName from goods g left join type t on g.type_id = t.id where g.status = 1 limit #{offset}, #{pageSize}")
    public List<Goods> getGoodsByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    
    @Select("select g.*, t.name as typeName from goods g left join type t on g.type_id = t.id where g.status = 1 and (g.type_id = #{typeId} or g.type_id in (select id from type where pid = #{typeId})) limit #{offset}, #{pageSize}")
    public List<Goods> getGoodsByTypePage(@Param("typeId") int typeId, @Param("offset") int offset, @Param("pageSize") int pageSize);
    
    @Select("select g.*, t.name as typeName from goods g left join type t on g.type_id = t.id where g.status = 1 and g.name like concat('%', #{keyword}, '%') limit #{offset}, #{pageSize}")
    public List<Goods> searchGoodsPage(@Param("keyword") String keyword, @Param("offset") int offset, @Param("pageSize") int pageSize);
    
    @Select("select * from goods where status = 1 and (type_id = #{typeId} or type_id in (select id from type where pid = #{typeId}))")
    public List<Goods> getGoodsByType(int typeId);
    
    @Select("select * from goods where id = #{id}")
    public Goods getGoodsById(int id);
    
    @Select("select * from goods where status = 1 and name like concat('%', #{keyword}, '%')")
    public List<Goods> searchGoods(String keyword);
    
    @Select("select g.*, t.name as typeName from goods g left join type t on g.type_id = t.id where g.status = 1 order by g.addtime desc limit 8")
    public List<Goods> getNewGoods();
    
    @Select("select g.*, t.name as typeName from goods g left join type t on g.type_id = t.id where g.status = 1 order by g.sales desc limit 8")
    public List<Goods> getTopSellGoods();
    
    @Select("select g.*, t.name as typeName from goods g left join type t on g.type_id = t.id where g.status = 1 order by g.sales desc limit #{offset}, #{pageSize}")
    public List<Goods> getTopSellGoodsPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    
    @Select("select g.*, t.name as typeName from goods g left join type t on g.type_id = t.id where g.status = 1 order by g.addtime desc limit #{offset}, #{pageSize}")
    public List<Goods> getNewGoodsPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    
    @Update("update goods set sales = sales + #{count} where id = #{id}")
    public void increaseSales(@Param("id") int id, @Param("count") int count);
    
    @Update("update goods set stock = stock - #{count} where id = #{id} and stock >= #{count}")
    public void decreaseStock(@Param("id") int id, @Param("count") int count);
    
    @Update("update goods set stock = stock + #{count} where id = #{id}")
    public void restoreStock(@Param("id") int id, @Param("count") int count);
    
    @Select("select count(*) from goods where status = 1")
    public int getGoodsCount();
    
    @Select("select count(*) from goods where status = 1 and (type_id = #{typeId} or type_id in (select id from type where pid = #{typeId}))")
    public int getGoodsCountByType(@Param("typeId") int typeId);
    
    @Select("select count(*) from goods where status = 1 and name like concat('%', #{keyword}, '%')")
    public int searchGoodsCount(@Param("keyword") String keyword);
    
    @Select("select count(*) from goods where stock <= #{threshold}")
    public int getLowStockCount(@Param("threshold") int threshold);

    @Select("select coalesce(sum(stock), 0) from goods")
    public int getTotalStock();

    @Select("select * from goods where stock <= #{threshold} order by stock asc")
    public List<Goods> getLowStockGoods(@Param("threshold") int threshold);

    // ===== 管理后台专用（不过滤 status）=====
    @Select("select g.*, t.name as typeName from goods g left join type t on g.type_id = t.id limit #{offset}, #{pageSize}")
    public List<Goods> getGoodsByPageAdmin(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Select("select count(*) from goods")
    public int getGoodsCountAdmin();

    @Update("update goods set status = #{status} where id = #{id}")
    public void updateGoodsStatus(@Param("id") int id, @Param("status") int status);
    
    @Insert("insert into goods (name, cover, image1, image2, price, intro, stock, status, type_id) values (#{name}, #{cover}, #{image1}, #{image2}, #{price}, #{intro}, #{stock}, #{status}, #{typeId})")
    public void addGoods(Goods goods);
    
    @Update("update goods set name = #{name}, cover = #{cover}, image1 = #{image1}, image2 = #{image2}, price = #{price}, intro = #{intro}, stock = #{stock}, type_id = #{typeId} where id = #{id}")
    public void updateGoods(Goods goods);
    
    @Delete("delete from goods where id = #{id}")
    public void deleteGoods(int id);
}