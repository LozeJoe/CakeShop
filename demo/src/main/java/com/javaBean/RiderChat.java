package com.javaBean;

/**
 * 骑手与用户聊天消息实体
 */
public class RiderChat {
    private int id;
    private String orderId;     // 关联订单号
    private String sender;      // rider / user
    private String senderName;  // 发送者名称
    private String content;     // 消息内容
    private String createTime;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
