package com.service;

import com.javaBean.Type;
import com.mapper.TypeMapper;
import javax.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TypeServiceImpl implements TypeService {

    @Resource
    private TypeMapper typeMapper;

    @Override
    public List<Type> getAllTypes() {
        return typeMapper.getAllTypes();
    }

    @Override
    public Type getTypeById(int id) {
        return typeMapper.getTypeById(id);
    }

    @Override
    public int getTypeCount() {
        return typeMapper.getTypeCount();
    }

    @Override
    public void addType(Type type) {
        typeMapper.addType(type);
    }

    @Override
    public void updateType(Type type) {
        typeMapper.updateType(type);
    }

    @Override
    public void deleteType(int id) {
        typeMapper.deleteType(id);
    }
}