package com.service;

import com.javaBean.Type;
import java.util.List;

public interface TypeService {
    List<Type> getAllTypes();
    Type getTypeById(int id);
    int getTypeCount();
    void addType(Type type);
    void updateType(Type type);
    void deleteType(int id);
}