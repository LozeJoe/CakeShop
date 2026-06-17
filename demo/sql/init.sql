-- ==============================================
-- CakeShop 蛋糕系统 - Docker 初始化脚本
-- 由 MySQL docker-entrypoint-initdb.d 自动执行
-- ==============================================

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 删除现有表（按依赖顺序）
DROP TABLE IF EXISTS `recommend`;
DROP TABLE IF EXISTS `orderitem`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `cart`;
DROP TABLE IF EXISTS `favorite`;
DROP TABLE IF EXISTS `review`;
DROP TABLE IF EXISTS `goods`;
DROP TABLE IF EXISTS `type`;
DROP TABLE IF EXISTS `rider`;
DROP TABLE IF EXISTS `admin_log`;
DROP TABLE IF EXISTS `user`;

-- 启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ==============================================
-- 用户表
-- ==============================================
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码(MD5)',
  `name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `isadmin` varchar(1) DEFAULT '0' COMMENT '是否管理员(0/1)',
  `isvalidate` varchar(1) DEFAULT '0' COMMENT '是否验证(0/1)',
  `regtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ==============================================
-- 分类表
-- ==============================================
CREATE TABLE `type` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `pid` int(11) DEFAULT '0' COMMENT '父分类ID',
  `sort` int(11) DEFAULT '0' COMMENT '排序号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ==============================================
-- 商品表
-- ==============================================
CREATE TABLE `goods` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(100) NOT NULL COMMENT '商品名称',
  `cover` varchar(255) DEFAULT NULL COMMENT '封面图片',
  `image1` varchar(255) DEFAULT NULL COMMENT '图片1',
  `image2` varchar(255) DEFAULT NULL COMMENT '图片2',
  `image3` varchar(255) DEFAULT NULL COMMENT '图片3',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `intro` text COMMENT '商品简介',
  `stock` int(11) DEFAULT '0' COMMENT '库存数量',
  `sales` int(11) DEFAULT '0' COMMENT '销量',
  `type_id` int(11) DEFAULT '0' COMMENT '分类ID',
  `addtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ==============================================
-- 订单表
-- ==============================================
CREATE TABLE `orders` (
  `id` varchar(50) NOT NULL COMMENT '订单ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `address` varchar(255) NOT NULL COMMENT '收货地址',
  `total` decimal(10,2) NOT NULL COMMENT '订单总价',
  `amount` int(11) NOT NULL COMMENT '商品总数量',
  `status` int(11) DEFAULT '1' COMMENT '订单状态',
  `paytype` int(11) DEFAULT NULL COMMENT '支付方式',
  `datetime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `rider_id` int(11) DEFAULT '0' COMMENT '骑手ID',
  `rider_income` decimal(10,2) DEFAULT '0.00' COMMENT '配送费',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==============================================
-- 订单项表
-- ==============================================
CREATE TABLE `orderitem` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` varchar(50) NOT NULL COMMENT '订单ID',
  `goods_id` int(11) NOT NULL COMMENT '商品ID',
  `price` float DEFAULT NULL COMMENT '单价',
  `amount` int(11) DEFAULT NULL COMMENT '数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ==============================================
-- 购物车表
-- ==============================================
CREATE TABLE `cart` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
  `good_id` varchar(255) NOT NULL COMMENT '商品ID',
  `user_name` varchar(255) NOT NULL COMMENT '用户名',
  `intro` varchar(255) NOT NULL COMMENT '简介',
  `amount` int(11) DEFAULT NULL COMMENT '数量',
  `price` float(10,2) DEFAULT NULL COMMENT '单价',
  `total_price` float(10,2) DEFAULT NULL COMMENT '总价',
  `cover` varchar(255) DEFAULT NULL COMMENT '封面',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ==============================================
-- 推荐表
-- ==============================================
CREATE TABLE `recommend` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '推荐ID',
  `type` tinyint(1) DEFAULT NULL COMMENT '推荐类型',
  `goods_id` int(11) DEFAULT NULL COMMENT '商品ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ==============================================
-- 评论表
-- ==============================================
CREATE TABLE `review` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `goods_id` int(11) NOT NULL COMMENT '商品ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户名',
  `content` varchar(500) NOT NULL COMMENT '评论内容',
  `rating` int(1) DEFAULT '5' COMMENT '评分 1-5',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ==============================================
-- 收藏表
-- ==============================================
CREATE TABLE `favorite` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `goods_id` int(11) NOT NULL COMMENT '商品ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ==============================================
-- 骑手表
-- ==============================================
CREATE TABLE `rider` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `id_card` varchar(20) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `level` int(1) DEFAULT '1',
  `status` int(1) DEFAULT '1',
  `total_orders` int(11) DEFAULT '0',
  `total_income` decimal(10,2) DEFAULT '0.00',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==============================================
-- 后台操作日志表
-- ==============================================
CREATE TABLE `admin_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `admin_name` varchar(50) DEFAULT NULL COMMENT '管理员',
  `action` varchar(50) DEFAULT NULL COMMENT '操作',
  `target` varchar(255) DEFAULT NULL COMMENT '目标',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ==============================================
-- 初始数据
-- ==============================================

-- 用户数据
INSERT INTO `user` (`username`, `password`, `name`, `email`, `phone`, `address`, `isadmin`, `isvalidate`) VALUES
('admin', '21232f297a57a5a743894a0e4a801fc3', '管理员', 'admin@cakeshop.com', '13800138000', '管理员地址', '1', '1'),
('vili', '202cb962ac59075b964b07152d234b70', '普通用户', 'vili@cakeshop.com', '13900139000', '用户地址', '0', '1');

-- 分类数据
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

-- 商品数据
INSERT INTO `goods` (`name`, `cover`, `image1`, `image2`, `image3`, `price`, `intro`, `stock`, `sales`, `type_id`) VALUES
('经典巧克力蛋糕', '/picture/1-1.jpg', '/picture/1-1.jpg', '/picture/1-2.jpg', '/picture/1-3.jpg', 168.00, '精选比利时黑巧克力，浓郁丝滑，口感细腻', 100, 50, 4),
('草莓慕斯蛋糕', '/picture/2-1.jpg', '/picture/2-1.jpg', '/picture/2-2.jpg', '/picture/2-3.jpg', 188.00, '新鲜草莓搭配轻盈慕斯，入口即化', 80, 30, 5),
('纽约芝士蛋糕', '/picture/3-1.jpg', '/picture/3-1.jpg', '/picture/3-2.jpg', '/picture/3-3.jpg', 198.00, '浓郁芝士风味，经典纽约配方', 60, 25, 6),
('法式牛角包', '/picture/4-1.jpg', '/picture/4-1.jpg', '/picture/4-2.jpg', '/picture/4-3.jpg', 18.00, '法式经典，层次分明，黄油香浓', 200, 150, 7),
('日式红豆面包', '/picture/5-1.jpg', '/picture/5-1.jpg', '/picture/5-2.jpg', '/picture/5-3.jpg', 12.00, '日式松软面包，红豆沙内馅', 150, 80, 8),
('经典马卡龙礼盒', '/picture/6-1.jpg', '/picture/6-1.jpg', '/picture/6-2.jpg', '/picture/6-3.jpg', 88.00, '法式马卡龙，多种口味组合', 100, 45, 9),
('意式提拉米苏', '/picture/7-1.jpg', '/picture/7-1.jpg', '/picture/7-2.jpg', '/picture/7-3.jpg', 38.00, '经典意式配方，咖啡酒香浓郁', 50, 60, 10),
('抹茶千层蛋糕', '/picture/8-1.jpg', '/picture/8-1.jpg', '/picture/8-2.jpg', '/picture/8-3.jpg', 178.00, '日本宇治抹茶，清新回甘', 70, 35, 5);

-- 推荐数据
INSERT INTO `recommend` (`type`, `goods_id`) VALUES
(1, 1),
(1, 2),
(2, 4),
(2, 5),
(3, 6);

-- 评论数据
INSERT INTO `review` (`goods_id`, `user_id`, `user_name`, `content`, `rating`, `create_time`) VALUES
(1, 2, 'vili', '巧克力味道非常浓郁，入口即化，绝对是巧克力爱好者的首选！', 5, '2026-06-01 10:30:00'),
(1, 1, '管理员', '店内招牌产品，比利时进口巧克力原料，品质保证。', 5, '2026-06-01 14:20:00'),
(2, 2, 'vili', '草莓很新鲜，慕斯轻盈不腻，夏天吃太合适了。', 4, '2026-06-02 09:15:00'),
(3, 2, 'vili', '芝士味很正，分量也足，回购了好几次。', 5, '2026-06-02 16:40:00'),
(4, 1, '管理员', '经典法式工艺，层层酥脆，早餐搭配咖啡绝佳。', 5, '2026-06-03 08:00:00'),
(7, 2, 'vili', '提拉米苏的咖啡酒味恰到好处，不会太甜。', 4, '2026-06-03 11:30:00'),
(8, 1, '管理员', '宇治抹茶粉制作，茶香清新，千层皮薄如蝉翼。', 5, '2026-06-04 10:00:00');

-- 收藏数据
INSERT INTO `favorite` (`user_id`, `goods_id`, `create_time`) VALUES
(2, 1, '2026-06-01 15:00:00'),
(2, 3, '2026-06-02 20:00:00'),
(2, 8, '2026-06-03 12:00:00');

-- 订单数据
INSERT INTO `orders` (`id`, `user_id`, `name`, `phone`, `address`, `total`, `amount`, `status`, `paytype`, `datetime`) VALUES
('20260601001', 2, 'vili', '13900139000', '上海市浦东新区', 356.00, 2, 5, 1, '2026-06-01 15:30:00'),
('20260603002', 2, 'vili', '13900139000', '上海市浦东新区', 198.00, 1, 2, 2, '2026-06-03 09:00:00'),
('20260604003', 1, '管理员', '13800138000', '北京市朝阳区', 88.00, 1, 2, 1, '2026-06-04 14:00:00');

-- 订单项数据
INSERT INTO `orderitem` (`order_id`, `goods_id`, `price`, `amount`) VALUES
('20260601001', 1, 168.00, 1),
('20260601001', 2, 188.00, 1),
('20260603002', 3, 198.00, 1),
('20260604003', 6, 88.00, 1);

-- 骑手数据
INSERT INTO `rider` (`username`, `password`, `name`, `phone`, `id_card`, `level`, `status`, `total_orders`, `total_income`) VALUES
('rider1', '123', '张骑手', '13800001111', '310000199001010001', 5, 1, 156, 3120.00),
('rider2', '123', '李骑手', '13800002222', '310000199002020002', 4, 1, 89, 1780.00);
