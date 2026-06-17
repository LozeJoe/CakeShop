---
name: gsap-timeline
description: GSAP 时间线：timeline() 序列编排、位置参数、标签、嵌套、播放控制。用于需要多步骤编排的动画场景。
---
# GSAP Timeline — 官方技能

## 创建时间线
```javascript
const tl = gsap.timeline({ defaults: { duration: 0.5, ease: "power2.out" } });
tl.to(".a", { x: 100 })
  .to(".b", { y: 50 })
  .to(".c", { opacity: 0 });
```
Tween 默认按顺序依次执行。用**位置参数**控制精确时机。

## 位置参数（第三参数）
- **绝对**：`1` — 从 1 秒开始
- **相对**：`"+=0.5"` — 上一步结束后 0.5s；`"-=0.2"` — 提前 0.2s
- **标签**：`"labelName+=0.3"` — 标签后 0.3s
- **对齐**：`"<"` — 与上一步同时开始；`">"` — 上一步结束时（默认）

```javascript
tl.to(".a", { x: 100 }, 0);
tl.to(".b", { y: 50 }, "+=0.5");
tl.to(".c", { opacity: 0 }, "<"); // 与 .b 同时开始
```

## 标签
```javascript
tl.addLabel("intro", 0);
tl.to(".a", { x: 100 }, "intro");
tl.play("intro"); // 从 intro 开始播放
```

## 嵌套
```javascript
const master = gsap.timeline();
const child = gsap.timeline();
child.to(".a", { x: 100 }).to(".b", { y: 50 });
master.add(child, 0);
```

## 播放控制
- `tl.play()` / `tl.pause()` / `tl.reverse()` / `tl.restart()`
- `tl.time(2)` — 跳到 2 秒
- `tl.progress(0.5)` — 跳到 50%
- `tl.kill()` — 销毁

## 最佳实践
- ✅ 用时间线替代 delay 链条
- ✅ 将 ScrollTrigger 放在时间线上，不要放在子 tween 上
- ❌ 不要把 ScrollTrigger 嵌套在时间线子 tween 中
