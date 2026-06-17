-- ==============================================
-- 数据库升级脚本
-- 基于现有 cookieshop.sql 进行修改
-- 添加支持热销、新品、订单取消、订单状态管理功能
-- ==============================================

USE `cookieshop`;

-- ==============================================
-- 1. 修改 goods 表 - 添加销量和创建时间字段
-- ==============================================
ALTER TABLE `goods` 
ADD COLUMN `sales` INT(11) NOT NULL DEFAULT '0' COMMENT '销量' AFTER `stock`,
ADD COLUMN `addtime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间' AFTER `sales`,
ADD INDEX `idx_sales` (`sales`),
ADD INDEX `idx_addtime` (`addtime`);

-- ==============================================
-- 2. 修改 order 表 - 完善订单状态字段
-- 状态说明: 1-待付款 2-已付款 3-已发货 4-已完成 5-已取消
-- ==============================================
ALTER TABLE `order` 
MODIFY COLUMN `status` INT(1) NOT NULL DEFAULT '1' COMMENT '订单状态:1-待付款 2-已付款 3-已发货 4-已完成 5-已取消',
ADD INDEX `idx_status` (`status`);

-- ==============================================
-- 3. 修改 user 表 - 添加注册时间字段
-- ==============================================
ALTER TABLE `user` 
ADD COLUMN `regtime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间' AFTER `isvalidate`;

-- ==============================================
-- 4. 修改 type 表 - 添加父分类和排序字段
-- ==============================================
ALTER TABLE `type` 
ADD COLUMN `pid` INT(11) NOT NULL DEFAULT '0' COMMENT '父分类ID' AFTER `name`,
ADD COLUMN `sort` INT(11) NOT NULL DEFAULT '0' COMMENT '排序号' AFTER `pid`,
ADD INDEX `idx_pid` (`pid`);

-- ==============================================
-- 5. 更新商品数据 - 设置销量和添加时间（示例数据）
-- ==============================================
UPDATE `goods` SET `sales` = FLOOR(RAND() * 200) + 10, `addtime` = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY);

-- ==============================================
-- 6. 更新分类数据 - 设置父分类和排序
-- ==============================================
UPDATE `type` SET `pid` = 0, `sort` = `id`;

-- ==============================================
-- 7. 更新订单数据 - 确保状态值正确
-- ==============================================
UPDATE `order` SET `status` = 4 WHERE `status` = 3;
UPDATE `order` SET `status` = 3 WHERE `status` = 2;
UPDATE `order` SET `status` = 2 WHERE `status` = 1;

-- ==============================================
-- 完成
-- ==============================================
SELECT '数据库升级完成' AS result;
