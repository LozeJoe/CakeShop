package com.service;

import com.javaBean.RiderChat;
import com.mapper.RiderChatMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * 骑手聊天服务接口，定义骑手与用户聊天相关业务方法。
 */
@Service
public class RiderChatService {

    @Resource
    private RiderChatMapper riderChatMapper;

    /**
     * 发送聊天消息。
     */
    public void sendMessage(String orderId, String sender, String senderName, String content) {
        RiderChat chat = new RiderChat();
        chat.setOrderId(orderId);
        chat.setSender(sender);
        chat.setSenderName(senderName);
        chat.setContent(content);
        riderChatMapper.addMessage(chat);
    }

    public List<RiderChat> getMessages(String orderId) {
        return riderChatMapper.getMessagesByOrderId(orderId);
    }

    public List<RiderChat> getRecentChats(int riderId) {
        return riderChatMapper.getRecentChats(riderId);
    }
}
