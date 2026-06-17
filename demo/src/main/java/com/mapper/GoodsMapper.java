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
    @Select("select g.*, t.name as typeName from goods g left join type t on g.type_id = t.id")
    public List<Goods> getAllGoods();
    
    @Select("select g.*, t.name as typeName from goods g left join type t on g.type_id = t.id limit #{offset}, #{pageSize}")
    public List<Goods> getGoodsByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    
    @Select("select g.*, t.name as typeName from goods g left join type t on g.type_id = t.id where g.type_id = #{typeId} or g.type_id in (select id from type where pid = #{typeId}) limit #{offset}, #{pageSize}")
    public List<Goods> getGoodsByTypePage(@Param("typeId") int typeId, @Param("offset") int offset, @Param("pageSize") int pageSize);
    
    @Select("select g.*, t.name as typeName from goods g left join type t on g.type_id = t.id where g.name like concat('%', #{keyword}, '%') limit #{offset}, #{pageSize}")
    public List<Goods> searchGoodsPage(@Param("keyword") String keyword, @Param("offset") int offset, @Param("pageSize") int pageSize);
    
    @Select("select * from goods where type_id = #{typeId} or type_id in (select id from type where pid = #{typeId})")
    public List<Goods> getGoodsByType(int typeId);
    
    @Select("select * from goods where id = #{id}")
    public Goods getGoodsById(int id);
    
    @Select("select * from goods where name like concat('%', #{keyword}, '%')")
    public List<Goods> searchGoods(String keyword);
    
    @Select("select g.*, t.name as typeName from goods g left join type t on g.type_id = t.id order by g.addtime desc limit 8")
    public List<Goods> getNewGoods();
    
    @Select("select g.*, t.name as typeName from goods g left join type t on g.type_id = t.id order by g.sales desc limit 8")
    public List<Goods> getTopSellGoods();
    
    @Select("select g.*, t.name as typeName from goods g left join type t on g.type_id = t.id order by g.sales desc limit #{offset}, #{pageSize}")
    public List<Goods> getTopSellGoodsPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    
    @Select("select g.*, t.name as typeName from goods g left join type t on g.type_id = t.id order by g.addtime desc limit #{offset}, #{pageSize}")
    public List<Goods> getNewGoodsPage(@Param("offset") int offset, @Param("pageSize") int pageSize);
    
    @Update("update goods set sales = sales + #{count} where id = #{id}")
    public void increaseSales(@Param("id") int id, @Param("count") int count);
    
    @Update("update goods set stock = stock - #{count} where id = #{id} and stock >= #{count}")
    public void decreaseStock(@Param("id") int id, @Param("count") int count);
    
    @Update("update goods set stock = stock + #{count} where id = #{id}")
    public void restoreStock(@Param("id") int id, @Param("count") int count);
    
    @Select("select count(*) from goods")
    public int getGoodsCount();
    
    @Select("select count(*) from goods where type_id = #{typeId} or type_id in (select id from type where pid = #{typeId})")
    public int getGoodsCountByType(@Param("typeId") int typeId);
    
    @Select("select count(*) from goods where name like concat('%', #{keyword}, '%')")
    public int searchGoodsCount(@Param("keyword") String keyword);
    
    @Select("select count(*) from goods where stock <= #{threshold}")
    public int getLowStockCount(@Param("threshold") int threshold);
    
    @Insert("insert into goods (name, cover, image1, image2, price, intro, stock, type_id) values (#{name}, #{cover}, #{image1}, #{image2}, #{price}, #{intro}, #{stock}, #{typeId})")
    public void addGoods(Goods goods);
    
    @Update("update goods set name = #{name}, cover = #{cover}, image1 = #{image1}, image2 = #{image2}, price = #{price}, intro = #{intro}, stock = #{stock}, type_id = #{typeId} where id = #{id}")
    public void updateGoods(Goods goods);
    
    @Delete("delete from goods where id = #{id}")
    public void deleteGoods(int id);
}