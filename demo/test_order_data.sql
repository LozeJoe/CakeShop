-- ==============================================
-- 订单测试数据脚本
-- 模拟用户下单、付款、取消订单流程
-- ==============================================

USE `cookieshop`;

-- ==============================================
-- 测试数据说明
-- ==============================================
-- status字段说明:
-- 1 - 待付款
-- 2 - 已付款  
-- 3 - 已发货
-- 4 - 已完成
-- 5 - 已取消

-- ==============================================
-- 1. 待付款订单 (status=1)
-- 用户 vili 下单但未付款
-- ==============================================
INSERT INTO `order` (`id`, `total`, `amount`, `status`, `paytype`, `name`, `phone`, `address`, `datetime`, `user_id`) VALUES
('TEST20240527001', '337', '3', '1', NULL, 'vili', '1344444444', '上海市浦东新区xxx路xxx号', '2024-05-27 10:30:00', '2');

INSERT INTO `orderitem` (`price`, `amount`, `goods_id`, `order_id`) VALUES
('299', '1', '9', 'TEST20240527001'),
('38', '1', '11', 'TEST20240527001');

-- ==============================================
-- 2. 已付款订单 (status=2)
-- 用户 vili 已付款，等待发货
-- ==============================================
INSERT INTO `order` (`id`, `total`, `amount`, `status`, `paytype`, `name`, `phone`, `address`, `datetime`, `user_id`) VALUES
('TEST20240527002', '68', '2', '2', '1', 'vili', '1344444444', '上海市浦东新区xxx路xxx号', '2024-05-27 11:00:00', '2');

INSERT INTO `orderitem` (`price`, `amount`, `goods_id`, `order_id`) VALUES
('36', '1', '12', 'TEST20240527002'),
('32', '1', '13', 'TEST20240527002');

-- ==============================================
-- 3. 已发货订单 (status=3)
-- 用户 Chenzhihuang 已付款，商家已发货
-- ==============================================
INSERT INTO `order` (`id`, `total`, `amount`, `status`, `paytype`, `name`, `phone`, `address`, `datetime`, `user_id`) VALUES
('TEST20240526001', '598', '2', '3', '2', '陈志煌', '13067311085', '广东省深圳市南山区xxx大道xxx号', '2024-05-26 14:20:00', '37');

INSERT INTO `orderitem` (`price`, `amount`, `goods_id`, `order_id`) VALUES
('299', '2', '9', 'TEST20240526001');

-- ==============================================
-- 4. 已完成订单 (status=4)
-- 用户 admin 订单已完成
-- ==============================================
INSERT INTO `order` (`id`, `total`, `amount`, `status`, `paytype`, `name`, `phone`, `address`, `datetime`, `user_id`) VALUES
('TEST20240525001', '152', '4', '4', '1', '管理员', '1333333333', '北京市朝阳区xxx街道xxx号', '2024-05-25 09:15:00', '1');

INSERT INTO `orderitem` (`price`, `amount`, `goods_id`, `order_id`) VALUES
('38', '2', '11', 'TEST20240525001'),
('39', '2', '14', 'TEST20240525001');

-- ==============================================
-- 5. 已取消订单 (status=5)
-- 用户 vili 取消了待付款订单
-- ==============================================
INSERT INTO `order` (`id`, `total`, `amount`, `status`, `paytype`, `name`, `phone`, `address`, `datetime`, `user_id`) VALUES
('TEST20240527003', '299', '1', '5', NULL, 'vili', '1344444444', '上海市浦东新区xxx路xxx号', '2024-05-27 09:00:00', '2');

INSERT INTO `orderitem` (`price`, `amount`, `goods_id`, `order_id`) VALUES
('299', '1', '15', 'TEST20240527003');

-- ==============================================
-- 6. 已取消订单 (status=5)
-- 用户 vili 取消了已付款订单（还未发货）
-- ==============================================
INSERT INTO `order` (`id`, `total`, `amount`, `status`, `paytype`, `name`, `phone`, `address`, `datetime`, `user_id`) VALUES
('TEST20240524001', '87', '3', '5', '2', 'vili', '1344444444', '上海市浦东新区xxx路xxx号', '2024-05-24 16:30:00', '2');

INSERT INTO `orderitem` (`price`, `amount`, `goods_id`, `order_id`) VALUES
('28', '1', '10', 'TEST20240524001'),
('36', '1', '12', 'TEST20240524001'),
('23', '1', '13', 'TEST20240524001');

-- ==============================================
-- 完成
-- ==============================================
SELECT '测试数据插入完成' AS result;
SELECT '待付款订单数:', COUNT(*) FROM `order` WHERE status = 1
UNION ALL
SELECT '已付款订单数:', COUNT(*) FROM `order` WHERE status = 2
UNION ALL
SELECT '已发货订单数:', COUNT(*) FROM `order` WHERE status = 3
UNION ALL
SELECT '已完成订单数:', COUNT(*) FROM `order` WHERE status = 4
UNION ALL
SELECT '已取消订单数:', COUNT(*) FROM `order` WHERE status = 5;
