-- CakeShop 数据库更新脚本 — 添加新字段
-- 请在 MySQL 客户端中执行

USE cookieshop;

ALTER TABLE `order`
  ADD COLUMN IF NOT EXISTS `delivery_time` varchar(100) DEFAULT NULL AFTER `datetime`,
  ADD COLUMN IF NOT EXISTS `latitude` double DEFAULT 0 AFTER `delivery_time`,
  ADD COLUMN IF NOT EXISTS `longitude` double DEFAULT 0 AFTER `latitude`,
  ADD COLUMN IF NOT EXISTS `commission` decimal(10,2) DEFAULT 0.00 AFTER `longitude`,
  ADD COLUMN IF NOT EXISTS `review_rating` int(1) DEFAULT 0 AFTER `commission`,
  ADD COLUMN IF NOT EXISTS `review_content` varchar(500) DEFAULT NULL AFTER `review_rating`;
