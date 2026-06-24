package com.mapper;

import com.javaBean.Type;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;


/**
 * TypeMapper接口，MyBatis Plus数据访问层。
 */
@Mapper
public interface TypeMapper {
    @Select("select * from type")
    /**
     * 获取所有分类列表。
     */
    public List<Type> getAllTypes();
    
    @Select("select * from type where id = #{id}")
    /**
     * 根据ID查询分类。
     */
    public Type getTypeById(int id);
    
    @Select("select count(*) from type")
    /**
     * 查询获取数据。
     */
    public int getTypeCount();
    
    @Insert("insert into type (name, pid, sort) values (#{name}, #{pid}, #{sort})")
    /**
     * 新增数据。
     */
    public void addType(Type type);
    
    @Update("update type set name = #{name} where id = #{id}")
    /**
     * 更新数据。
     */
    public void updateType(Type type);
    
    @Delete("delete from type where id = #{id}")
    /**
     * 删除数据。
     */
    public void deleteType(int id);
}