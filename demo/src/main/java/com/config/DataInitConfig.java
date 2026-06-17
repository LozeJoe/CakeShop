package com.config;

import com.javaBean.Goods;
import com.javaBean.Type;
import com.javaBean.User;
import com.mapper.GoodsMapper;
import com.mapper.TypeMapper;
import com.mapper.UserMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

@Configuration
public class DataInitConfig {

    @Resource
    private UserMapper userMapper;

    @Resource
    private TypeMapper typeMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private DataSource dataSource;

    @javax.annotation.PostConstruct
    public void initData() {
        migrateOrderTable();
        migrateUserTable();
        migrateMessageTable();
        initUsers();
        initTypes();
        initGoods();
    }

    /** Add new columns to the `order` table if they don't already exist. */
    private void migrateOrderTable() {
        String[] alterStatements = {
            "ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `delivery_time` varchar(100) DEFAULT NULL AFTER `datetime`",
            "ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `latitude` double DEFAULT 0 AFTER `delivery_time`",
            "ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `longitude` double DEFAULT 0 AFTER `latitude`",
            "ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `commission` decimal(10,2) DEFAULT 0.00 AFTER `longitude`",
            "ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `review_rating` int(1) DEFAULT 0 AFTER `commission`",
            "ALTER TABLE `order` ADD COLUMN IF NOT EXISTS `review_content` varchar(500) DEFAULT NULL AFTER `review_rating`"
        };
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            for (String sql : alterStatements) {
                try {
                    stmt.execute(sql);
                } catch (Exception ignored) {
                    // Column may already exist — ignore error
                }
            }
        } catch (Exception e) {
            System.out.println("Order table migration skipped: " + e.getMessage());
        }
    }

    /** Add rider fields to the `user` table. */
    private void migrateUserTable() {
        String[] alterStatements = {
            "ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `id_card` varchar(20) DEFAULT NULL AFTER `status`",
            "ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `avatar` varchar(255) DEFAULT NULL AFTER `id_card`",
            "ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `level` int(1) DEFAULT 1 AFTER `avatar`",
            "ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `total_orders` int(11) DEFAULT 0 AFTER `level`",
            "ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `total_income` decimal(10,2) DEFAULT 0.00 AFTER `total_orders`",
            "ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `balance` decimal(10,2) DEFAULT 0.00 AFTER `total_income`"
        };
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            for (String sql : alterStatements) {
                try {
                    stmt.execute(sql);
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            System.out.println("User table migration skipped: " + e.getMessage());
        }
    }

    /** Create rider_message table if not exists. */
    private void migrateMessageTable() {
        String sql = "CREATE TABLE IF NOT EXISTS `rider_message` ("
            + "`id` int(11) NOT NULL AUTO_INCREMENT,"
            + "`rider_id` int(11) NOT NULL COMMENT '骑手用户ID',"
            + "`type` varchar(20) NOT NULL COMMENT '消息类型: order/system/income',"
            + "`title` varchar(200) DEFAULT NULL COMMENT '消息标题',"
            + "`content` text COMMENT '消息内容',"
            + "`is_read` tinyint(1) DEFAULT 0 COMMENT '是否已读 0=未读 1=已读',"
            + "`create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',"
            + "PRIMARY KEY (`id`),"
            + "KEY `idx_rider_id` (`rider_id`),"
            + "KEY `idx_type` (`type`)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='骑手消息通知表'";
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            System.out.println("Message table migration skipped: " + e.getMessage());
        }
    }

    private void initUsers() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean seeded = false;

        try (Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {

            // 1) 检查是否有用户数据
            java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM user");
            rs.next();
            int userCount = rs.getInt(1);
            rs.close();

            if (userCount == 0) {
                // 首次初始化 — 全部用 BCrypt
                userMapper.register("admin", encoder.encode("admin123"), "管理员", "13800138000", "北京市朝阳区", "admin@cakeshop.com", "1");
                userMapper.register("vili", encoder.encode("Vili1234!"), "vili", "1344444444", "上海市浦东新区", "vili@cakeshop.com", "0");
                userMapper.register("rider1", encoder.encode("Rider1234!"), "张三", "13900139000", "配送站", "rider1@cakeshop.com", "2");
                System.out.println("Users initialized with BCrypt passwords");
                seeded = true;
            }
        } catch (Exception e) {
            System.out.println("User init check failed: " + e.getMessage());
        }

        // 2) 如果已有用户（旧数据），升级密码 + 确保种子用户完整
        if (!seeded) {
            try (Connection conn = dataSource.getConnection()) {
                // 2a) 升级旧 MD5 密码为 BCrypt
                try (java.sql.PreparedStatement ps = conn.prepareStatement(
                         "UPDATE user SET password = ? WHERE LENGTH(password) = 32 AND password REGEXP '^[0-9a-f]{32}$' AND username = ?")) {
                    String[][] seeds = {{"admin","admin123"},{"vili","Vili1234!"},{"rider1","Rider1234!"}};
                    for (String[] s : seeds) {
                        ps.setString(1, encoder.encode(s[1]));
                        ps.setString(2, s[0]);
                        if (ps.executeUpdate() > 0) System.out.println("Upgraded password for: " + s[0]);
                    }
                }
                // 2b) 重置非 BCrypt 密码的种子用户
                try (java.sql.PreparedStatement ps = conn.prepareStatement(
                         "UPDATE user SET password = ?, name = ?, phone = ?, address = ?, email = ?, isadmin = ? WHERE username = ? AND (password NOT LIKE '$2a$%' OR password IS NULL)")) {
                    String[][] seeds = {
                        {"admin123", "管理员", "13800138000", "北京市朝阳区", "admin@cakeshop.com", "1", "admin"},
                        {"Vili1234!", "vili", "1344444444", "上海市浦东新区", "vili@cakeshop.com", "0", "vili"},
                        {"Rider1234!", "张三", "13900139000", "配送站", "rider1@cakeshop.com", "2", "rider1"}
                    };
                    for (String[] row : seeds) {
                        ps.setString(1, encoder.encode(row[0]));
                        for (int i = 1; i <= 6; i++) ps.setString(i + 1, row[i]);
                        if (ps.executeUpdate() > 0) System.out.println("Fixed non-BCrypt password for: " + row[6]);
                    }
                }
                // 2c) INSERT IGNORE 确保种子用户存在（用 BCrypt 密码）
                try (java.sql.PreparedStatement ps = conn.prepareStatement(
                         "INSERT IGNORE INTO user (username, password, name, phone, address, email, isadmin) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    String[][] inserts = {
                        {"admin",    encoder.encode("admin123"),    "管理员", "13800138000", "北京市朝阳区", "admin@cakeshop.com", "1"},
                        {"vili",     encoder.encode("Vili1234!"),  "vili",   "1344444444",  "上海市浦东新区", "vili@cakeshop.com",  "0"},
                        {"rider1",   encoder.encode("Rider1234!"), "张三",  "13900139000", "配送站",        "rider1@cakeshop.com", "2"}
                    };
                    for (String[] row : inserts) {
                        for (int i = 0; i < row.length; i++) ps.setString(i + 1, row[i]);
                        if (ps.executeUpdate() > 0) System.out.println("Inserted missing seed user: " + row[0]);
                    }
                }
                // 2c) 升级其他 MD5 用户（如测试中创建的 testuser）
                try (java.sql.PreparedStatement ps = conn.prepareStatement(
                         "UPDATE user SET password = ? WHERE LENGTH(password) = 32 AND password REGEXP '^[0-9a-f]{32}$' AND username NOT IN ('admin','vili','rider1')")) {
                    ps.setString(1, encoder.encode("Test1234!"));
                    int other = ps.executeUpdate();
                    if (other > 0) System.out.println("Upgraded " + other + " additional MD5 passwords");
                }
            } catch (Exception e) {
                System.out.println("User upgrade failed: " + e.getMessage());
            }
        }
        seedMessages();
    }

    /** Seed sample messages for the rider. */
    private void seedMessages() {
        String insertSql = "INSERT IGNORE INTO `rider_message` (`rider_id`, `type`, `title`, `content`, `create_time`) VALUES "
            + "(3, 'order', '新订单提醒 #20260603002', '您有新订单待接单，订单号 20260603002，配送地址：上海市浦东新区', '2026-06-03 09:00:00'),"
            + "(3, 'order', '新订单提醒 #20260604003', '您有新订单待接单，订单号 20260604003，配送地址：北京市朝阳区', '2026-06-04 14:00:00'),"
            + "(3, 'income', '配送收入到账', '订单 #20260601001 配送完成，收入 ¥35.60 已到账', '2026-06-01 16:00:00'),"
            + "(3, 'system', '平台公告：端午节配送安排', '端午节期间（6月25-27日）配送费调整为平时1.5倍，请合理安排配送时间', '2026-06-20 10:00:00'),"
            + "(3, 'system', '系统升级通知', '配送系统将于6月15日凌晨2:00-4:00进行升级维护，期间无法接单', '2026-06-14 08:00:00')";
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(insertSql);
        } catch (Exception ignored) {
        }
    }

    private void initTypes() {
        List<Type> types = typeMapper.getAllTypes();
        if (types == null || types.isEmpty()) {
            String[][] typeData = {{"冰淇淋系列", "0", "1"}, {"零食系列", "0", "2"}, {"儿童系列", "0", "3"}, {"法式系列", "0", "4"}, {"经典系列", "0", "5"}, {"节日系列", "0", "6"}};
            for (String[] data : typeData) {
                Type type = new Type();
                type.setName(data[0]);
                type.setPid(Integer.parseInt(data[1]));
                type.setSort(Integer.parseInt(data[2]));
                typeMapper.addType(type);
            }
            System.out.println("Types initialized");
        }
    }

    private void initGoods() {
        List<Goods> goods = goodsMapper.getAllGoods();
        if (goods == null || goods.isEmpty()) {
            Goods[] goodsArray = {
                createGoods("草莓冰淇淋", "/picture/9-1.jpg", "/picture/9-2.jpg", "/picture/9-3.jpg", 299.00, "甜郁草莓配合冰淇淋的丝滑口感", 100, 156, 1),
                createGoods("玫瑰舒芙蕾", "/picture/10-1.jpg", "/picture/10-2.jpg", "/picture/10-3.jpg", 28.00, "优选法国芝士,奶香浓郁", 80, 89, 3),
                createGoods("半熟芝士", "/picture/11-1.jpg", "/picture/11-1.jpg", "/picture/11-1.jpg", 38.00, "借鉴日本温泉煮鸡蛋的做法", 94, 72, 2),
                createGoods("青森芝士条", "/picture/12-1.jpg", "/picture/1-2.jpg", "/picture/12-1.jpg", 36.00, "青森芝士和风轻拂", 95, 320, 2),
                createGoods("蜂蜜蛋糕", "/picture/13-1.jpg", "/picture/13-1.jpg", "/picture/13-1.jpg", 36.00, "蛋黄与蜂蜜的曼妙之旅", 93, 185, 2),
                createGoods("意大利芝士饼干", "/picture/14-1.jpg", "/picture/14-1.jpg", "/picture/14-1.jpg", 39.00, "帕玛森芝士制作", 95, 67, 2),
                createGoods("小熊乐园", "/picture/8-1.jpg", "/picture/8-2.jpg", "/picture/8-3.jpg", 299.00, "可爱小熊主题蛋糕", 95, 143, 3),
                createGoods("抹茶千层", "/picture/matcha.jpg", "/picture/matcha.jpg", "/picture/matcha.jpg", 178.00, "日本宇治抹茶", 70, 95, 4)
            };
            for (Goods g : goodsArray) {
                goodsMapper.addGoods(g);
            }
            System.out.println("Goods initialized");
        }
    }

    private Goods createGoods(String name, String cover, String image1, String image2, double price, String intro, int stock, int sales, int typeId) {
        Goods goods = new Goods();
        goods.setName(name);
        goods.setCover(cover);
        goods.setImage1(image1);
        goods.setImage2(image2);
        goods.setPrice(price);
        goods.setIntro(intro);
        goods.setStock(stock);
        goods.setSales(sales);
        goods.setTypeId(typeId);
        return goods;
    }
}
