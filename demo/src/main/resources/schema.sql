-- Cake Shop 数据库初始化脚本

-- 创建数据库（如果不存在）并设置字符集
CREATE DATABASE IF NOT EXISTS `cookieshop` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER DATABASE `cookieshop` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `cookieshop`;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 删除现有表（按依赖顺序）
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

-- 启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 创建用户表
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `isadmin` varchar(1) DEFAULT '0' COMMENT '是否管理员',
  `isvalidate` varchar(1) DEFAULT '0' COMMENT '是否验证',
  `status` int(1) DEFAULT '0' COMMENT '0正常 1冻结',
  `regtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- 创建分类表
CREATE TABLE `type` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `pid` int(11) DEFAULT '0' COMMENT '父分类ID',
  `sort` int(11) DEFAULT '0' COMMENT '排序号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- 创建商品表
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

-- 创建订单表
CREATE TABLE `order` (
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

-- 创建订单项表
CREATE TABLE `orderitem` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` varchar(50) NOT NULL COMMENT '订单ID',
  `goods_id` int(11) NOT NULL COMMENT '商品ID',
  `price` float DEFAULT NULL COMMENT '单价',
  `amount` int(11) DEFAULT NULL COMMENT '数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- 创建购物车表
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

-- 创建推荐表
CREATE TABLE `recommend` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '推荐ID',
  `type` tinyint(1) DEFAULT NULL COMMENT '推荐类型',
  `goods_id` int(11) DEFAULT NULL COMMENT '商品ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- 创建评论表
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

-- 创建收藏表
CREATE TABLE `favorite` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `goods_id` int(11) NOT NULL COMMENT '商品ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- 创建骑手表
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

-- 创建后台操作日志表
CREATE TABLE `admin_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `admin_name` varchar(50) DEFAULT NULL COMMENT '管理员',
  `action` varchar(50) DEFAULT NULL COMMENT '操作',
  `target` varchar(255) DEFAULT NULL COMMENT '目标',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
