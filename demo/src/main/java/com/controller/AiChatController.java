package com.controller;

import com.service.AiChatService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class AiChatController {

    @Resource
    private AiChatService aiChatService;

    @RequestMapping("/api/chat")
    @ResponseBody
    public String chat(@RequestParam("message") String message) {
        if (message == null || message.trim().isEmpty()) {
            return "请说点什么吧~ 😊";
        }
        return aiChatService.chat(message.trim());
    }
}
