下面给你一套完整可直接运行的示例，包含：可拖动的形象 + 点击弹出聊天窗口 + 简单聊天界面，你只需要把自己的形象图片替换进去即可。

---

## 一、整体实现思路
1.  **可拖动形象**：用一个固定在页面角落的 `div` 实现，通过 JS 监听鼠标事件实现拖拽。
2.  **聊天弹窗**：默认隐藏，点击形象时显示，右上角带关闭按钮。
3.  **基础聊天逻辑**：模拟用户发送消息，也可以对接你之前的火山引擎 API。

---

## 二、完整代码示例（HTML + CSS + JS）
```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AI客服示例</title>
    <style>
        /* 全局样式重置 */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: "Microsoft YaHei", sans-serif;
        }

        body {
            background-color: #f5f7fa;
            height: 1500px; /* 让页面有滚动条，方便测试拖动 */
        }

        /* 可拖动的客服形象 */
        #ai-avatar {
            position: fixed;
            bottom: 30px;
            left: 30px;
            width: 80px;
            height: 80px;
            cursor: move;
            z-index: 999;
            /* 替换为你的形象图片 */
            background: url("https://img0.baidu.com/it/u=3919111901,3817111100&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500") center/cover no-repeat;
            border-radius: 50%;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            user-select: none; /* 防止选中文字 */
        }

        /* 聊天弹窗容器 */
        #chat-window {
            position: fixed;
            bottom: 120px;
            left: 30px;
            width: 360px;
            height: 500px;
            background-color: #fff;
            border-radius: 16px;
            box-shadow: 0 8px 24px rgba(0,0,0,0.15);
            display: none; /* 默认隐藏 */
            flex-direction: column;
            z-index: 1000;
            overflow: hidden;
        }

        /* 弹窗头部 */
        .chat-header {
            background: linear-gradient(135deg, #ff7eb3, #ff758c);
            color: white;
            padding: 16px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .chat-header .title {
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .chat-header .close-btn {
            background: none;
            border: none;
            color: white;
            font-size: 20px;
            cursor: pointer;
        }

        /* 聊天内容区域 */
        .chat-content {
            flex: 1;
            padding: 16px;
            overflow-y: auto;
            background-color: #f9fafc;
        }

        /* 消息气泡 */
        .message {
            margin-bottom: 12px;
            display: flex;
        }

        .message.user {
            justify-content: flex-end;
        }

        .message.ai {
            justify-content: flex-start;
        }

        .message .bubble {
            max-width: 70%;
            padding: 10px 14px;
            border-radius: 18px;
            line-height: 1.5;
            font-size: 14px;
        }

        .message.user .bubble {
            background-color: #6366f1;
            color: white;
            border-bottom-right-radius: 4px;
        }

        .message.ai .bubble {
            background-color: #e5e7eb;
            color: #1f2937;
            border-bottom-left-radius: 4px;
        }

        /* 输入区域 */
        .chat-input-area {
            padding: 12px;
            border-top: 1px solid #eee;
            display: flex;
            gap: 8px;
        }

        .chat-input-area input {
            flex: 1;
            padding: 10px 14px;
            border: 1px solid #ddd;
            border-radius: 20px;
            outline: none;
            font-size: 14px;
        }

        .chat-input-area button {
            width: 40px;
            height: 40px;
            border: none;
            border-radius: 50%;
            background-color: #ff758c;
            color: white;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        /* 底部版权信息 */
        .chat-footer {
            text-align: center;
            font-size: 12px;
            color: #999;
            padding: 8px;
            background-color: #f9fafc;
        }
    </style>
</head>
<body>
    <!-- 可拖动的AI客服形象 -->
    <div id="ai-avatar"></div>

    <!-- 聊天弹窗 -->
    <div id="chat-window">
        <div class="chat-header">
            <div class="title">
                <span>💬</span>
                <span>AI 客服 - 小甜</span>
            </div>
            <button class="close-btn">&times;</button>
        </div>
        <div class="chat-content" id="chat-content">
            <!-- 初始AI欢迎消息 -->
            <div class="message ai">
                <div class="bubble">你好呀！我是蛋糕店的AI客服小甜🍰，有什么可以帮你的吗？</div>
            </div>
        </div>
        <div class="chat-input-area">
            <input type="text" id="chat-input" placeholder="输入消息...">
            <button id="send-btn">➤</button>
        </div>
        <div class="chat-footer">
            Powered by 🔥火山引擎 | 配置API
        </div>
    </div>

    <script>
        // ---------------- 1. 实现拖动功能 ----------------
        const avatar = document.getElementById('ai-avatar');
        let isDragging = false;
        let offsetX, offsetY;

        avatar.addEventListener('mousedown', (e) => {
            isDragging = true;
            // 计算鼠标与元素左上角的偏移
            offsetX = e.clientX - avatar.offsetLeft;
            offsetY = e.clientY - avatar.offsetTop;
            avatar.style.cursor = 'grabbing';
        });

        document.addEventListener('mousemove', (e) => {
            if (!isDragging) return;
            // 限制拖动范围在视口内
            const x = e.clientX - offsetX;
            const y = e.clientY - offsetY;
            const maxX = window.innerWidth - avatar.offsetWidth;
            const maxY = window.innerHeight - avatar.offsetHeight;

            avatar.style.left = Math.max(0, Math.min(x, maxX)) + 'px';
            avatar.style.bottom = 'auto'; // 改为top定位，避免和bottom冲突
            avatar.style.top = Math.max(0, Math.min(y, maxY)) + 'px';
        });

        document.addEventListener('mouseup', () => {
            isDragging = false;
            avatar.style.cursor = 'move';
        });

        // ---------------- 2. 聊天弹窗控制 ----------------
        const chatWindow = document.getElementById('chat-window');
        const closeBtn = document.querySelector('.close-btn');

        // 点击形象打开弹窗
        avatar.addEventListener('click', (e) => {
            // 只有在非拖动状态下点击才打开弹窗
            if (!isDragging) {
                chatWindow.style.display = 'flex';
                // 弹窗位置跟随形象
                const avatarRect = avatar.getBoundingClientRect();
                chatWindow.style.left = avatarRect.left + 'px';
                chatWindow.style.bottom = (window.innerHeight - avatarRect.top + 20) + 'px';
                chatWindow.style.top = 'auto';
            }
        });

        // 关闭弹窗
        closeBtn.addEventListener('click', () => {
            chatWindow.style.display = 'none';
        });

        // ---------------- 3. 基础聊天功能 ----------------
        const chatContent = document.getElementById('chat-content');
        const chatInput = document.getElementById('chat-input');
        const sendBtn = document.getElementById('send-btn');

        // 模拟AI回复（这里可以替换为你的火山引擎API调用）
        const aiReplies = {
            "有什么蛋糕?": "哇~我们店里的蛋糕种类超丰富的！🎂经典款有 **巧克力熔岩蛋糕**（外酥里嫩，一口爆浆超满足！）、**草莓奶油蛋糕**（新鲜草莓+轻盈奶油，酸甜不腻~）；最近还上了新品 **海盐焦糖慕斯**（咸甜交织，口感丝滑）和 **芒果流心蛋糕**（热带水果风味，果香超浓郁！）🍓🍫🥭"
        };

        function addMessage(text, isUser = false) {
            const messageDiv = document.createElement('div');
            messageDiv.className = `message ${isUser ? 'user' : 'ai'}`;
            messageDiv.innerHTML = `<div class="bubble">${text}</div>`;
            chatContent.appendChild(messageDiv);
            // 滚动到底部
            chatContent.scrollTop = chatContent.scrollHeight;
        }

        function sendMessage() {
            const text = chatInput.value.trim();
            if (!text) return;

            // 添加用户消息
            addMessage(text, true);
            chatInput.value = '';

            // 模拟AI回复（可替换为API调用）
            setTimeout(() => {
                const reply = aiReplies[text] || "不好意思，我暂时还理解不了这个问题哦~ 可以换个方式问我吗？😊";
                addMessage(reply);
            }, 600);
        }

        sendBtn.addEventListener('click', sendMessage);
        chatInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') sendMessage();
        });
    </script>
</body>
</html>
```

---

## 三、关键部分说明 & 如何对接你的火山引擎API
### 1. 替换你的形象图片
在 CSS 里找到 `#ai-avatar` 的 `background` 样式，把 `url()` 里的链接换成你自己的图片地址即可：
```css
#ai-avatar {
  /* 其他样式不变 */
  background: url("你的图片地址.png") center/cover no-repeat;
}
```

### 2. 把模拟回复换成真实API调用
在 JS 里找到 `// 模拟AI回复` 部分，把 `setTimeout` 换成你的 API 请求（以火山引擎为例）：
```javascript
async function getAIReply(userMessage) {
    const API_KEY = "你的DEFAULT_API_KEY";
    const API_URL = "https://ark.cn-beijing.volces.com/api/v3/chat/completions";
    const MODEL = "你的DEFAULT_MODEL";

    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${API_KEY}`
            },
            body: JSON.stringify({
                model: MODEL,
                messages: [
                    { role: "system", content: "你是蛋糕店的客服小甜，语气要亲切可爱。" },
                    { role: "user", content: userMessage }
                ]
            })
        });
        const data = await response.json();
        return data.choices[0].message.content;
    } catch (error) {
        console.error("API请求失败：", error);
        return "网络有点波动，稍后再试吧~";
    }
}

// 替换 sendMessage 里的 setTimeout 部分：
async function sendMessage() {
    const text = chatInput.value.trim();
    if (!text) return;

    addMessage(text, true);
    chatInput.value = '';

    // 调用真实API
    const reply = await getAIReply(text);
    addMessage(reply);
}
```

---

## 四、部署到你的Spring Boot项目里
1.  把这段代码保存为 `chat.html`，或者直接写在你现有的 JSP/HTML 页面里。
2.  确保你的API Key和Model配置正确，并且API接口允许跨域（或者在后端写个代理接口转发请求，避免前端直接暴露API Key）。
3.  测试：拖动形象、点击打开弹窗、发送消息，看是否正常工作。

---

