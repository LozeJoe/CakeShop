-- H2 兼容建表脚本（MySQL 兼容模式）

DROP TABLE IF EXISTS `rider_message`;
DROP TABLE IF EXISTS `rider`;
DROP TABLE IF EXISTS `admin_log`;
DROP TABLE IF EXISTS `favorite`;
DROP TABLE IF EXISTS `review`;
DROP TABLE IF EXISTS `recommend`;
DROP TABLE IF EXISTS `orderitem`;
DROP TABLE IF EXISTS `order`;
DROP TABLE IF EXISTS `cart`;
DROP TABLE IF EXISTS `goods`;
DROP TABLE IF EXISTS `type`;
DROP TABLE IF EXISTS `user`;

-- 用户表
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `isadmin` varchar(1) DEFAULT '0',
  `isvalidate` varchar(1) DEFAULT '0',
  `status` int(1) DEFAULT '0',
  `regtime` datetime DEFAULT CURRENT_TIMESTAMP,
  `id_card` varchar(20) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `level` int(1) DEFAULT 1,
  `total_orders` int(11) DEFAULT 0,
  `total_income` decimal(10,2) DEFAULT 0.00,
  `balance` decimal(10,2) DEFAULT 0.00,
  PRIMARY KEY (`id`),
  UNIQUE KEY (`username`),
  UNIQUE KEY (`email`)
);

-- 分类表
CREATE TABLE `type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `pid` int(11) DEFAULT '0',
  `sort` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY (`name`)
);

-- 商品表
CREATE TABLE `goods` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `cover` varchar(255) DEFAULT NULL,
  `image1` varchar(255) DEFAULT NULL,
  `image2` varchar(255) DEFAULT NULL,
  `image3` varchar(255) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `intro` text DEFAULT NULL,
  `stock` int(11) DEFAULT '0',
  `sales` int(11) DEFAULT '0',
  `type_id` int(11) DEFAULT '0',
  `addtime` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

-- 订单表
CREATE TABLE `order` (
  `id` varchar(50) NOT NULL,
  `user_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `address` varchar(255) NOT NULL,
  `total` decimal(10,2) NOT NULL,
  `amount` int(11) NOT NULL,
  `status` int(11) DEFAULT '1',
  `paytype` int(11) DEFAULT NULL,
  `datetime` datetime DEFAULT CURRENT_TIMESTAMP,
  `delivery_time` varchar(100) DEFAULT NULL,
  `latitude` double DEFAULT 0,
  `longitude` double DEFAULT 0,
  `commission` decimal(10,2) DEFAULT 0.00,
  `review_rating` int(1) DEFAULT 0,
  `review_content` varchar(500) DEFAULT NULL,
  `rider_id` int(11) DEFAULT '0',
  `rider_income` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`id`)
);

-- 订单项表
CREATE TABLE `orderitem` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(50) NOT NULL,
  `goods_id` int(11) NOT NULL,
  `price` float DEFAULT NULL,
  `amount` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- 购物车表
CREATE TABLE `cart` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `good_id` varchar(255) NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `intro` varchar(255) NOT NULL,
  `amount` int(11) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `total_price` decimal(10,2) DEFAULT NULL,
  `cover` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- 推荐表
CREATE TABLE `recommend` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` tinyint(1) DEFAULT NULL,
  `goods_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- 评论表
CREATE TABLE `review` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `user_name` varchar(50) DEFAULT NULL,
  `content` varchar(500) NOT NULL,
  `rating` int(1) DEFAULT '5',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

-- 收藏表
CREATE TABLE `favorite` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `goods_id` int(11) NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

-- 骑手表
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
);

-- 后台操作日志表
CREATE TABLE `admin_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `admin_name` varchar(50) DEFAULT NULL,
  `action` varchar(50) DEFAULT NULL,
  `target` varchar(255) DEFAULT NULL,
  `ip` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

-- 骑手消息表
CREATE TABLE `rider_message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rider_id` int(11) NOT NULL,
  `type` varchar(20) NOT NULL,
  `title` varchar(200) DEFAULT NULL,
  `content` text DEFAULT NULL,
  `is_read` tinyint(1) DEFAULT 0,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

CREATE INDEX IF NOT EXISTS `idx_rider_id` ON `rider_message` (`rider_id`);
CREATE INDEX IF NOT EXISTS `idx_type` ON `rider_message` (`type`);

-- 骑手-用户聊天表
CREATE TABLE `rider_chat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(50) NOT NULL,
  `sender` varchar(10) NOT NULL,
  `sender_name` varchar(50) DEFAULT NULL,
  `content` text NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

CREATE INDEX IF NOT EXISTS `idx_chat_order` ON `rider_chat` (`order_id`);
