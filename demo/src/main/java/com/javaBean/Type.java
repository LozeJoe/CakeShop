package com.javaBean;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class Type {
    int id;
    
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称不超过 50 个字符")
    String name;
    
    int pid;
    int sort;
}