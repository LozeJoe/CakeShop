package com.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.javaBean.Goods;
import com.javaBean.Type;
import com.mapper.GoodsMapper;
import com.mapper.TypeMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

@Service
public class AiChatService {

    @Value("${deepseek.api.url}")
    private String apiUrl;

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.model}")
    private String model;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private TypeMapper typeMapper;

    private String systemPrompt;

    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        systemPrompt = buildSystemPrompt();
    }

    /**
     * 构建系统提示词，包含项目中所有商品和分类信息
     */
    private String buildSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("你是「CakeShop」蛋糕店的AI客服助手，名叫「小甜」。你的回答必须基于以下店铺真实信息，不得编造不存在的内容。\n\n");

        // 分类信息
        List<Type> types = typeMapper.getAllTypes();
        sb.append("【店铺分类】\n");
        for (Type t : types) {
            if (t.getPid() == 0) {
                sb.append("- ").append(t.getName()).append("（包含：");
                for (Type child : types) {
                    if (child.getPid() == t.getId()) {
                        sb.append(child.getName()).append("、");
                    }
                }
                if (sb.charAt(sb.length() - 1) == '、') sb.setLength(sb.length() - 1);
                sb.append("）\n");
            }
        }

        // 商品信息
        List<Goods> goods = goodsMapper.getAllGoods();
        sb.append("\n【在售商品列表】\n");
        for (Goods g : goods) {
            String typeName = "";
            for (Type t : types) {
                if (t.getId() == g.getTypeId()) { typeName = t.getName(); break; }
            }
            sb.append("- ").append(g.getName())
              .append(" | 价格：¥").append(String.format("%.2f", g.getPrice()))
              .append(" | 分类：").append(typeName)
              .append(" | 库存：").append(g.getStock()).append("件")
              .append(" | 简介：").append(g.getIntro())
              .append("\n");
        }

        sb.append("\n【店铺信息】\n");
        sb.append("- 店名：CakeShop（法式烘焙）\n");
        sb.append("- 特色：精选优质原料，匠心手作蛋糕\n");
        sb.append("- 地址：系统演示店铺\n");
        sb.append("- 营业时间：全天在线\n");

        sb.append("\n【回复规则】\n");
        sb.append("1. 只回答与蛋糕、甜品、店铺相关的问题。\n");
        sb.append("2. 推荐商品时必须从上述列表中选取，不得编造不存在商品。\n");
        sb.append("3. 回复要热情、简洁，控制在200字以内。\n");
        sb.append("4. 使用emoji让回复更生动。\n");
        sb.append("5. 如果用户问的问题超出范围，礼貌引导回店铺相关话题。\n");

        return sb.toString();
    }

    /**
     * 发送消息到 DeepSeek API（OpenAI 兼容格式）
     */
    public String chat(String userMessage) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoOutput(true);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(30000);

            // 构建请求体
            ObjectNode body = mapper.createObjectNode();
            body.put("model", model);

            ArrayNode messages = body.putArray("messages");

            ObjectNode sysMsg = mapper.createObjectNode();
            sysMsg.put("role", "system");
            sysMsg.put("content", systemPrompt);
            messages.add(sysMsg);

            ObjectNode userMsg = mapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);

            body.put("max_tokens", 500);
            body.put("temperature", 0.7);

            String jsonBody = mapper.writeValueAsString(body);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            if (code == 200) {
                try (Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8")) {
                    String response = scanner.useDelimiter("\\A").next();
                    JsonNode root = mapper.readTree(response);
                    return root.path("choices").get(0).path("message").path("content").asText();
                }
            } else {
                try (Scanner scanner = new Scanner(conn.getErrorStream(), "UTF-8")) {
                    String err = scanner.useDelimiter("\\A").next();
                    return "抱歉，AI服务暂时不可用，请稍后再试。";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "抱歉，我暂时无法回答，请稍后再试。😢";
        }
    }
}
