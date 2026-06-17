package com.javaBean;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class User {
    int id;
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度需在 2-50 之间")
    String username;
    
    @Size(max = 100, message = "密码不超过 100 个字符")
    String password;
    
    @Email(message = "邮箱格式不正确")
    String email;
    
    String isadmin;
    
    @Size(max = 50, message = "姓名不超过 50 个字符")
    String name;
    
    @Size(max = 20, message = "手机号不超过 20 个字符")
    String phone;
    
    @Size(max = 255, message = "地址不超过 255 个字符")
    String address;
    
    String sex;
    String isvalidate; // 0=未审核 1=已审核
    int status;        // 0=正常 1=冻结
    // 骑手字段（isadmin='2' 时为骑手）
    String idCard;     // 身份证号
    String avatar;     // 头像
    int level;         // 配送等级 1-5
    int totalOrders;    // 累计配送
    double totalIncome; // 累计收入
    double balance;     // 账户余额
}
