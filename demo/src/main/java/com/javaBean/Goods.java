package com.javaBean;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class Goods {
    int id;
    
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称不超过 100 个字符")
    String name;
    
    String cover;
    String image1;
    String image2;
    
    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格不能为负数")
    double price;
    
    @Size(max = 500, message = "简介不超过 500 个字符")
    String intro;
    
    @Min(value = 0, message = "库存不能为负数")
    int stock;
    
    int typeId;
    String typeName;
    int sales;
    int status;
    String addtime;
}