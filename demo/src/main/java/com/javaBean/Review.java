package com.javaBean;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class Review {
    int id;
    
    @NotNull(message = "商品ID不能为空")
    int goodsId;
    
    int userId;
    String userName;
    
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论不超过 500 个字符")
    String content;
    
    int rating; // 1-5 星
    int status;  // 0=待审核 1=已通过
    String createTime;
}
