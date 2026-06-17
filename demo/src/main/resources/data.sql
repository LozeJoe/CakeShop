INSERT INTO `user` (`username`, `password`, `name`, `email`, `phone`, `address`, `isadmin`, `isvalidate`) VALUES
('admin', '21232f297a57a5a743894a0e4a801fc3', '管理员', 'admin@cakeshop.com', '13800138000', '管理员地址', '1', '1'),
('vili', '202cb962ac59075b964b07152d234b70', '普通用户', 'vili@cakeshop.com', '13900139000', '用户地址', '0', '1');

INSERT INTO `type` (`name`, `pid`, `sort`) VALUES
('蛋糕', 0, 1),
('面包', 0, 2),
('甜品', 0, 3),
('生日蛋糕', 1, 1),
('慕斯蛋糕', 1, 2),
('芝士蛋糕', 1, 3),
('欧式面包', 2, 1),
('日式面包', 2, 2),
('马卡龙', 3, 1),
('提拉米苏', 3, 2);

INSERT INTO `goods` (`name`, `cover`, `image1`, `image2`, `image3`, `price`, `intro`, `stock`, `sales`, `type_id`) VALUES
('经典巧克力蛋糕', '/picture/1-1.jpg', '/picture/1-1.jpg', '/picture/1-2.jpg', '/picture/1-3.jpg', 168.00, '精选比利时黑巧克力，浓郁丝滑，口感细腻', 100, 50, 4),
('草莓慕斯蛋糕', '/picture/2-1.jpg', '/picture/2-1.jpg', '/picture/2-2.jpg', '/picture/2-3.jpg', 188.00, '新鲜草莓搭配轻盈慕斯，入口即化', 80, 30, 5),
('纽约芝士蛋糕', '/picture/3-1.jpg', '/picture/3-1.jpg', '/picture/3-2.jpg', '/picture/3-3.jpg', 198.00, '浓郁芝士风味，经典纽约配方', 60, 25, 6),
('法式牛角包', '/picture/4-1.jpg', '/picture/4-1.jpg', '/picture/4-2.jpg', '/picture/4-3.jpg', 18.00, '法式经典，层次分明，黄油香浓', 200, 150, 7),
('日式红豆面包', '/picture/5-1.jpg', '/picture/5-1.jpg', '/picture/5-2.jpg', '/picture/5-3.jpg', 12.00, '日式松软面包，红豆沙内馅', 150, 80, 8),
('经典马卡龙礼盒', '/picture/6-1.jpg', '/picture/6-1.jpg', '/picture/6-2.jpg', '/picture/6-3.jpg', 88.00, '法式马卡龙，多种口味组合', 100, 45, 9),
('意式提拉米苏', '/picture/7-1.jpg', '/picture/7-1.jpg', '/picture/7-2.jpg', '/picture/7-3.jpg', 38.00, '经典意式配方，咖啡酒香浓郁', 50, 60, 10),
('抹茶千层蛋糕', '/picture/8-1.jpg', '/picture/8-1.jpg', '/picture/8-2.jpg', '/picture/8-3.jpg', 178.00, '日本宇治抹茶，清新回甘', 70, 35, 5);

INSERT INTO `recommend` (`type`, `goods_id`) VALUES
(1, 1),
(1, 2),
(2, 4),
(2, 5),
(3, 6);

-- 评论样例
INSERT INTO `review` (`goods_id`, `user_id`, `user_name`, `content`, `rating`, `create_time`) VALUES
(1, 2, 'vili', '巧克力味道非常浓郁，入口即化，绝对是巧克力爱好者的首选！', 5, '2026-06-01 10:30:00'),
(1, 1, '管理员', '店内招牌产品，比利时进口巧克力原料，品质保证。', 5, '2026-06-01 14:20:00'),
(2, 2, 'vili', '草莓很新鲜，慕斯轻盈不腻，夏天吃太合适了。', 4, '2026-06-02 09:15:00'),
(3, 2, 'vili', '芝士味很正，分量也足，回购了好几次。', 5, '2026-06-02 16:40:00'),
(4, 1, '管理员', '经典法式工艺，层层酥脆，早餐搭配咖啡绝佳。', 5, '2026-06-03 08:00:00'),
(7, 2, 'vili', '提拉米苏的咖啡酒味恰到好处，不会太甜。', 4, '2026-06-03 11:30:00'),
(8, 1, '管理员', '宇治抹茶粉制作，茶香清新，千层皮薄如蝉翼。', 5, '2026-06-04 10:00:00');

-- 收藏样例
INSERT INTO `favorite` (`user_id`, `goods_id`, `create_time`) VALUES
(2, 1, '2026-06-01 15:00:00'),
(2, 3, '2026-06-02 20:00:00'),
(2, 8, '2026-06-03 12:00:00');

-- 订单样例
INSERT INTO `order` (`id`, `user_id`, `name`, `phone`, `address`, `total`, `amount`, `status`, `paytype`, `datetime`) VALUES
('20260601001', 2, 'vili', '13900139000', '上海市浦东新区', 356.00, 2, 5, 1, '2026-06-01 15:30:00'),
('20260603002', 2, 'vili', '13900139000', '上海市浦东新区', 198.00, 1, 2, 2, '2026-06-03 09:00:00'),
('20260604003', 1, '管理员', '13800138000', '北京市朝阳区', 88.00, 1, 2, 1, '2026-06-04 14:00:00');

-- 骑手样例
INSERT INTO `rider` (`username`, `password`, `name`, `phone`, `id_card`, `level`, `status`, `total_orders`, `total_income`) VALUES
('rider1', '123', '张骑手', '13800001111', '310000199001010001', 5, 1, 156, 3120.00),
('rider2', '123', '李骑手', '13800002222', '310000199002020002', 4, 1, 89, 1780.00);

-- 骑手账号同步到 user 表（isadmin='2'，登录验证用）
INSERT INTO `user` (`username`, `password`, `name`, `phone`, `address`, `isadmin`, `isvalidate`, `status`) VALUES
('rider1', '123', '张骑手', '13800001111', '', '2', '1', 0),
('rider2', '123', '李骑手', '13800002222', '', '2', '1', 0);

-- 订单项样例
INSERT INTO `orderitem` (`order_id`, `goods_id`, `price`, `amount`) VALUES
('20260601001', 1, 168.00, 1),
('20260601001', 2, 188.00, 1),
('20260603002', 3, 198.00, 1),
('20260604003', 6, 88.00, 1);
