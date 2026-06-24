package com.service;

import com.javaBean.Type;
import com.mapper.TypeMapper;
import javax.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;


/**
 * 分类服务实现类，提供分类的增删改查等业务逻辑实现。
 */
@Service
public class TypeServiceImpl implements TypeService {

    @Resource
    private TypeMapper typeMapper;

    /**
     * 获取所有分类列表。
     */
    @Override
    public List<Type> getAllTypes() {
        return typeMapper.getAllTypes();
    }

    /**
     * 根据ID查询分类。
     */
    @Override
    public Type getTypeById(int id) {
        return typeMapper.getTypeById(id);
    }

    /**
     * 查询获取数据。
     */
    @Override
    public int getTypeCount() {
        return typeMapper.getTypeCount();
    }

    /**
     * 新增数据。
     */
    @Override
    public void addType(Type type) {
        typeMapper.addType(type);
    }

    /**
     * 更新数据。
     */
    @Override
    public void updateType(Type type) {
        typeMapper.updateType(type);
    }

    /**
     * 删除数据。
     */
    @Override
    public void deleteType(int id) {
        typeMapper.deleteType(id);
    }
}