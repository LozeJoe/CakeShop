package com.service;

import com.javaBean.Type;
import java.util.List;


/**
 * 分类服务接口，定义分类增删改查等业务方法。
 */
public interface TypeService {
    List<Type> getAllTypes();
    Type getTypeById(int id);
    int getTypeCount();
    void addType(Type type);
    void updateType(Type type);
    void deleteType(int id);
}