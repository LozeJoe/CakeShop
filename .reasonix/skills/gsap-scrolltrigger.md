---
name: gsap-scrolltrigger
description: GSAP ScrollTrigger：滚动驱动动画、元素固定(pin)、scrub、触发器。需要注册插件后使用。
---
# GSAP ScrollTrigger — 官方技能

## 注册插件
```javascript
gsap.registerPlugin(ScrollTrigger);
```

## 基本用法
```javascript
gsap.to(".box", {
  x: 500,
  scrollTrigger: {
    trigger: ".box",
    start: "top center",   // trigger 顶部碰到视口中心
    end: "bottom center",
    toggleActions: "play reverse play reverse"
  }
});
```

## ScrollTrigger + Timeline（推荐模式）
```javascript
const tl = gsap.timeline({
  scrollTrigger: {
    trigger: ".container",
    start: "top top",
    end: "+=2000",
    scrub: 1,
    pin: true
  }
});
tl.to(".a", { x: 100 }).to(".b", { y: 50 }).to(".c", { opacity: 0 });
```

## 关键配置
| 属性 | 说明 |
|------|------|
| `trigger` | 触发器元素 |
| `start/end` | `"triggerPos viewportPos"`，如 `"top 80%"`, `"+=300"`, `"max"` |
| `scrub` | `true` = 滚动驱动；数字 = 平滑延迟（秒） |
| `pin` | `true` = 固定触发元素 |
| `pinSpacing` | 默认 `true`，添加占位避免布局塌陷 |
| `markers` | `true` = 开发标记（生产环境必须删除） |
| `once` | `true` = 只触发一次 |
| `toggleClass` | 切换 CSS class |

## start/end 格式
`"triggerPosition viewportPosition"`：`"top top"`, `"center center"`, `"bottom 80%"`  
相对值：`"+=300"`(300px), `"+=100%"`(视口高度), `"max"`(最大滚动)  
clamp：`"clamp(top bottom)"` 防止越界

## 刷新和清理
```javascript
ScrollTrigger.refresh(); // DOM/布局变化后调用（resize 自动处理）
ScrollTrigger.getAll().forEach(t => t.kill()); // 清理全部
```

## 最佳实践
- ✅ 先 `gsap.registerPlugin(ScrollTrigger)`
- ✅ DOM 变化后调用 `ScrollTrigger.refresh()`
- ✅ ScrollTrigger 放在 timeline 或顶层 tween，不放子 tween 里
- ❌ 不要把 ScrollTrigger 嵌套在父时间线的子 tween 中
- ❌ `scrub` 和 `toggleActions` 不要同时使用
- ❌ 生产环境删除 `markers: true`
