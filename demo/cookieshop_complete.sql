-- ==============================================
-- Cake Shop 完整数据库脚本
-- 包含：用户、分类、商品、订单、订单项、购物车数据
-- ==============================================

-- 创建数据库（如果不存在）并设置字符集
CREATE DATABASE IF NOT EXISTS `cookieshop` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `cookieshop`;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 删除现有表（按依赖顺序）
DROP TABLE IF EXISTS `recommend`;
DROP TABLE IF EXISTS `orderitem`;
DROP TABLE IF EXISTS `order`;
DROP TABLE IF EXISTS `cart`;
DROP TABLE IF EXISTS `goods`;
DROP TABLE IF EXISTS `type`;
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
  PRIMARY KEY (`id`),
  KEY `idx_type_id` (`type_id`),
  KEY `idx_sales` (`sales`),
  KEY `idx_addtime` (`addtime`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ==============================================
-- 订单表
-- status: 1-待付款 2-已付款 3-已发货 4-已完成 5-已取消
-- ==============================================
CREATE TABLE `order` (
  `id` varchar(50) NOT NULL COMMENT '订单ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `name` varchar(50) NOT NULL COMMENT '收货人姓名',
  `phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `address` varchar(255) NOT NULL COMMENT '收货地址',
  `total` decimal(10,2) NOT NULL COMMENT '订单总价',
  `amount` int(11) NOT NULL COMMENT '商品总数量',
  `status` int(11) DEFAULT '1' COMMENT '订单状态',
  `paytype` int(11) DEFAULT NULL COMMENT '支付方式(1-支付宝 2-微信)',
  `datetime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_datetime` (`datetime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==============================================
-- 订单项表
-- ==============================================
CREATE TABLE `orderitem` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` varchar(50) NOT NULL COMMENT '订单ID',
  `goods_id` int(11) NOT NULL COMMENT '商品ID',
  `price` decimal(10,2) DEFAULT NULL COMMENT '单价',
  `amount` int(11) DEFAULT NULL COMMENT '数量',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_goods_id` (`goods_id`)
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
  `price` decimal(10,2) DEFAULT NULL COMMENT '单价',
  `total_price` decimal(10,2) DEFAULT NULL COMMENT '总价',
  `cover` varchar(255) DEFAULT NULL COMMENT '封面',
  PRIMARY KEY (`id`),
  KEY `idx_user_name` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ==============================================
-- 推荐表
-- type: 1-首页推荐 2-热销推荐 3-新品推荐
-- ==============================================
CREATE TABLE `recommend` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '推荐ID',
  `type` tinyint(1) DEFAULT NULL COMMENT '推荐类型',
  `goods_id` int(11) DEFAULT NULL COMMENT '商品ID',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ==============================================
-- 插入测试数据
-- ==============================================

-- 用户数据
INSERT INTO `user` (`username`, `password`, `name`, `email`, `phone`, `address`, `isadmin`, `isvalidate`) VALUES
('admin', '21232f297a57a5a743894a0e4a801fc3', '管理员', 'admin@cakeshop.com', '13800138000', '北京市朝阳区xxx街道xxx号', '1', '1'),
('customer001', '202cb962ac59075b964b07152d234b70', '张三', 'zhangsan@cakeshop.com', '13900139001', '上海市浦东新区xxx路xxx号', '0', '1'),
('customer002', '202cb962ac59075b964b07152d234b70', '李四', 'lisi@cakeshop.com', '13900139002', '广州市天河区xxx街xxx号', '0', '1'),
('customer003', '202cb962ac59075b964b07152d234b70', '王五', 'wangwu@cakeshop.com', '13900139003', '深圳市南山区xxx大道xxx号', '0', '1');

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
('提拉米苏', 3, 2),
('奶油蛋糕', 1, 4),
('全麦面包', 2, 3);

-- 商品数据
INSERT INTO `goods` (`name`, `cover`, `image1`, `image2`, `image3`, `price`, `intro`, `stock`, `sales`, `type_id`, `addtime`) VALUES
('经典巧克力蛋糕', '/picture/chocolate.jpg', '/picture/chocolate.jpg', '/picture/chocolate2.jpg', '/picture/chocolate3.jpg', 168.00, '精选比利时黑巧克力，浓郁丝滑，口感细腻，适合生日庆祝', 100, 156, 4, '2024-01-15 10:00:00'),
('草莓慕斯蛋糕', '/picture/strawberry.jpg', '/picture/strawberry.jpg', '/picture/strawberry2.jpg', '/picture/strawberry3.jpg', 188.00, '新鲜草莓搭配轻盈慕斯，入口即化，清新甜美', 80, 89, 5, '2024-02-20 14:30:00'),
('纽约芝士蛋糕', '/picture/cheese.jpg', '/picture/cheese.jpg', '/picture/cheese2.jpg', '/picture/cheese3.jpg', 198.00, '浓郁芝士风味，经典纽约配方，醇厚绵密', 60, 72, 6, '2024-01-20 09:00:00'),
('法式牛角包', '/picture/croissant.jpg', '/picture/croissant.jpg', '/picture/croissant2.jpg', '/picture/croissant3.jpg', 18.00, '法式经典，层次分明，黄油香浓，早餐首选', 200, 320, 7, '2024-01-10 08:00:00'),
('日式红豆面包', '/picture/redbean.jpg', '/picture/redbean.jpg', '/picture/redbean2.jpg', '/picture/redbean3.jpg', 12.00, '日式松软面包，香甜红豆沙内馅', 150, 185, 8, '2024-01-12 08:30:00'),
('经典马卡龙礼盒', '/picture/macaron.jpg', '/picture/macaron.jpg', '/picture/macaron2.jpg', '/picture/macaron3.jpg', 88.00, '法式马卡龙，6种口味组合，送礼佳品', 100, 67, 9, '2024-02-14 10:00:00'),
('意式提拉米苏', '/picture/tiramisu.jpg', '/picture/tiramisu.jpg', '/picture/tiramisu2.jpg', '/picture/tiramisu3.jpg', 38.00, '经典意式配方，咖啡酒香浓郁，回味无穷', 50, 143, 10, '2024-01-08 11:00:00'),
('抹茶千层蛋糕', '/picture/matcha.jpg', '/picture/matcha.jpg', '/picture/matcha2.jpg', '/picture/matcha3.jpg', 178.00, '日本宇治抹茶，清新回甘，层层美味', 70, 95, 5, '2024-03-01 10:00:00'),
('芒果奶油蛋糕', '/picture/mango.jpg', '/picture/mango.jpg', '/picture/mango2.jpg', '/picture/mango3.jpg', 158.00, '新鲜芒果搭配轻盈奶油，果香四溢', 90, 45, 11, '2024-03-10 14:00:00'),
('全麦吐司', '/picture/wholewheat.jpg', '/picture/wholewheat.jpg', '/picture/wholewheat2.jpg', '/picture/wholewheat3.jpg', 22.00, '100%全麦粉制作，健康营养，口感扎实', 120, 167, 12, '2024-02-25 07:30:00'),
('黑森林蛋糕', '/picture/blackforest.jpg', '/picture/blackforest.jpg', '/picture/blackforest2.jpg', '/picture/blackforest3.jpg', 218.00, '德国经典，樱桃与巧克力的完美结合', 50, 38, 4, '2024-02-01 10:00:00'),
('蓝莓芝士蛋糕', '/picture/blueberry.jpg', '/picture/blueberry.jpg', '/picture/blueberry2.jpg', '/picture/blueberry3.jpg', 208.00, '新鲜蓝莓与浓郁芝士的完美搭配', 60, 52, 6, '2024-03-05 09:00:00');

-- 推荐数据
INSERT INTO `recommend` (`type`, `goods_id`) VALUES
(1, 1),
(1, 2),
(1, 4),
(2, 4),
(2, 7),
(2, 1),
(2, 10),
(3, 8),
(3, 9),
(3, 12);

-- 订单数据（status: 1-待付款 2-已付款 3-已发货 4-已完成 5-已取消）
INSERT INTO `order` (`id`, `user_id`, `name`, `phone`, `address`, `total`, `amount`, `status`, `paytype`, `datetime`) VALUES
('2024031500001', 2, '张三', '13900139001', '上海市浦东新区xxx路xxx号', 168.00, 1, 4, 1, '2024-03-15 10:30:00'),
('2024031600002', 2, '张三', '13900139001', '上海市浦东新区xxx路xxx号', 206.00, 2, 3, 2, '2024-03-16 14:20:00'),
('2024031700003', 3, '李四', '13900139002', '广州市天河区xxx街xxx号', 88.00, 1, 2, 1, '2024-03-17 09:15:00'),
('2024031800004', 4, '王五', '13900139003', '深圳市南山区xxx大道xxx号', 376.00, 2, 1, NULL, '2024-03-18 16:45:00'),
('2024031900005', 2, '张三', '13900139001', '上海市浦东新区xxx路xxx号', 198.00, 1, 5, 1, '2024-03-19 11:00:00');

-- 订单项数据
INSERT INTO `orderitem` (`order_id`, `goods_id`, `price`, `amount`) VALUES
('2024031500001', 1, 168.00, 1),
('2024031600002', 4, 18.00, 2),
('2024031600002', 7, 38.00, 4),
('2024031700003', 6, 88.00, 1),
('2024031800004', 3, 198.00, 1),
('2024031800004', 8, 178.00, 1),
('2024031900005', 3, 198.00, 1);

-- 购物车数据
INSERT INTO `cart` (`good_id`, `user_name`, `intro`, `amount`, `price`, `total_price`, `cover`) VALUES
('1', 'customer001', '精选比利时黑巧克力，浓郁丝滑', 2, 168.00, 336.00, '/picture/chocolate.jpg'),
('4', 'customer001', '法式经典，层次分明，黄油香浓', 3, 18.00, 54.00, '/picture/croissant.jpg'),
('7', 'customer002', '经典意式配方，咖啡酒香浓郁', 1, 38.00, 38.00, '/picture/tiramisu.jpg'),
('8', 'customer003', '日本宇治抹茶，清新回甘', 1, 178.00, 178.00, '/picture/matcha.jpg');

-- ==============================================
-- 创建索引优化查询
-- ==============================================
CREATE INDEX idx_goods_type_sales ON goods(type_id, sales);
CREATE INDEX idx_order_user_status ON `order`(user_id, status);

-- ==============================================
-- 完成
-- ==============================================
SELECT '数据库初始化完成' AS result;
