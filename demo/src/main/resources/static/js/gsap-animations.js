/**
 * CakeShop — GSAP 动效增强
 * 用法: 在页面底部 <script src="/js/gsap-animations.js"></script>
 * 需要先加载 GSAP CDN: <script src="https://cdn.jsdelivr.net/npm/gsap@3.12.5/dist/gsap.min.js"></script>
 * <script src="https://cdn.jsdelivr.net/npm/gsap@3.12.5/dist/ScrollTrigger.min.js"></script>
 */
(function () {
  if (typeof gsap === "undefined") return;

  // 卡片悬浮
  document.querySelectorAll(".kpi-card, .product-card, .order-card").forEach(function (el) {
    el.addEventListener("mouseenter", function () {
      gsap.to(el, { y: -4, duration: 0.3, ease: "power2.out" });
    });
    el.addEventListener("mouseleave", function () {
      gsap.to(el, { y: 0, duration: 0.3, ease: "power2.out" });
    });
  });

  // 滚动揭示
  document.querySelectorAll(".fade-up").forEach(function (el) {
    gsap.from(el, {
      y: 32,
      autoAlpha: 0,
      duration: 0.6,
      ease: "power3.out",
      scrollTrigger: { trigger: el, start: "top 88%", once: true },
    });
  });

  // KPI 数字跳动
  document.querySelectorAll(".kpi-value [data-count]").forEach(function (el) {
    var target = parseInt(el.getAttribute("data-count"), 10) || 0;
    gsap.from(el, {
      textContent: 0,
      duration: 1.5,
      ease: "power2.out",
      snap: { textContent: 1 },
      scrollTrigger: { trigger: el.closest(".kpi-card"), start: "top 90%", once: true },
      onUpdate: function () {
        el.textContent = Math.round(el.textContent).toLocaleString();
      },
    });
  });

  // 导航栏滚动效果
  var navbar = document.querySelector(".navbar");
  if (navbar) {
    var lastY = 0;
    window.addEventListener("scroll", function () {
      var y = window.scrollY;
      if (y > 50 && y > lastY) {
        gsap.to(navbar, { y: -80, duration: 0.3, ease: "power2.in" });
      } else {
        gsap.to(navbar, { y: 0, duration: 0.3, ease: "power2.out" });
      }
      lastY = y;
    });
  }

  console.log("🍰 CakeShop GSAP animations ready");
})();
