package com.mapper;

import com.javaBean.Type;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

@Mapper
public interface TypeMapper {
    @Select("select * from type")
    public List<Type> getAllTypes();
    
    @Select("select * from type where id = #{id}")
    public Type getTypeById(int id);
    
    @Select("select count(*) from type")
    public int getTypeCount();
    
    @Insert("insert into type (name, pid, sort) values (#{name}, #{pid}, #{sort})")
    public void addType(Type type);
    
    @Update("update type set name = #{name} where id = #{id}")
    public void updateType(Type type);
    
    @Delete("delete from type where id = #{id}")
    public void deleteType(int id);
}