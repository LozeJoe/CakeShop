---
name: gsap-core
description: GSAP 核心 API：gsap.to()/from()/fromTo()、缓动、延迟、stagger、响应式 matchMedia()。用于 JS 动画、DOM/CSS/SVG 动画。
---
# GSAP Core — 官方技能

## 核心方法
- `gsap.to(targets, vars)` — 从当前状态动画到目标值（最常用）
- `gsap.from(targets, vars)` — 从目标值动画到当前状态（入场效果）
- `gsap.fromTo(targets, fromVars, toVars)` — 明确起始和结束值
- `gsap.set(targets, vars)` — 立即应用（duration=0）

vars 对象中属性使用 **camelCase**：`backgroundColor`, `marginTop`, `rotationX`

## Transform 别名（优先使用，性能更好）
| 属性 | 效果 |
|------|------|
| `x`, `y`, `z` | translateX/Y/Z（默认 px） |
| `xPercent`, `yPercent` | 基于百分比的位移 |
| `scale`, `scaleX`, `scaleY` | 缩放 |
| `rotation` | 旋转（默认 deg） |
| `rotationX`, `rotationY` | 3D 旋转 |
| `skewX`, `skewY` | 倾斜 |
| `autoAlpha` | 透明度 + visibility（推荐替代 opacity） |

## 常用 vars
- `duration` — 秒（默认 0.5）
- `ease` — `"power1.out"`（默认）、`"power3.inOut"`、`"back.out(1.7)"`、`"elastic.out(1,0.3)"`、`"none"`
- `stagger` — 数字或 `{amount:0.3, from:"center"}`
- `repeat: -1` — 无限循环；`yoyo: true` — 来回
- `onComplete`, `onStart`, `onUpdate` — 回调

## 相对值
`x: "+=20"`, `rotation: "-=30"`, `x: "*=2"`

## 默认值
`gsap.defaults({ duration: 0.6, ease: "power2.out" });`

## matchMedia（响应式 + 无障碍）
```javascript
let mm = gsap.matchMedia();
mm.add("(min-width: 800px)", () => {
  gsap.to(".box", { x: 500 });
  return () => { /* 清理 */ };
});
mm.revert(); // 卸载时调用
```
