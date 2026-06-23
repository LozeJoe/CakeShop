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
        migrateReviewTable();
        migrateGoodsTable();
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
            "ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `balance` decimal(10,2) DEFAULT 0.00 AFTER `total_income`",
            "ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `admin_role` varchar(20) DEFAULT 'admin' AFTER `balance`"
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

    /** Add status column to review table if it doesn't exist. */
    private void migrateReviewTable() {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE review ADD COLUMN IF NOT EXISTS status int(1) DEFAULT 1");
        } catch (Exception ignored) {}
    }

    /** Add status column to goods table if it doesn't exist. */
    private void migrateGoodsTable() {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE goods ADD COLUMN IF NOT EXISTS status int(1) DEFAULT 1");
        } catch (Exception ignored) {}
    }

    /** Create rider_message table if not exists (MySQL + H2 compatible). */
    private void migrateMessageTable() {
        String sql = "CREATE TABLE IF NOT EXISTS `rider_message` ("
            + "`id` int(11) NOT NULL AUTO_INCREMENT,"
            + "`rider_id` int(11) NOT NULL,"
            + "`type` varchar(20) NOT NULL,"
            + "`title` varchar(200) DEFAULT NULL,"
            + "`content` text DEFAULT NULL,"
            + "`is_read` tinyint(1) DEFAULT 0,"
            + "`create_time` datetime DEFAULT CURRENT_TIMESTAMP,"
            + "PRIMARY KEY (`id`)"
            + ")";
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            // Add indexes separately (ignored if already exist)
            try { stmt.execute("CREATE INDEX IF NOT EXISTS idx_rider_id ON rider_message (rider_id)"); } catch (Exception ignored) {}
            try { stmt.execute("CREATE INDEX IF NOT EXISTS idx_type ON rider_message (type)"); } catch (Exception ignored) {}
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
                // 首次初始化 — 直接用 SQL 确保 admin_role 写入
                String insertAdmin = "INSERT INTO user (username, password, name, phone, address, email, isadmin, admin_role) VALUES " +
                    "('admin', '" + encoder.encode("admin123") + "', '管理员', '13800138000', '北京市朝阳区', 'admin@cakeshop.com', '1', 'super_admin')";
                stmt.execute(insertAdmin);
                String insertUser = "INSERT INTO user (username, password, name, phone, address, email, isadmin) VALUES " +
                    "('vili', '" + encoder.encode("Vili1234!") + "', 'vili', '1344444444', '上海市浦东新区', 'vili@cakeshop.com', '0')";
                stmt.execute(insertUser);
                String insertRider = "INSERT INTO user (username, password, name, phone, address, email, isadmin) VALUES " +
                    "('rider1', '" + encoder.encode("Rider1234!") + "', '张三', '13900139000', '配送站', 'rider1@cakeshop.com', '2')";
                stmt.execute(insertRider);
                System.out.println("Users initialized with BCrypt passwords");
                seeded = true;
            }
        } catch (Exception e) {
            System.out.println("User init check failed: " + e.getMessage());
        }

        // 2) 如果已有用户（旧数据），升级密码 + 确保种子用户完整
        if (!seeded) {
            // 定义种子用户数据
            String[][] seedUsers = {
                {"admin",  "admin123",   "管理员", "13800138000", "北京市朝阳区", "admin@cakeshop.com", "1"},
                {"vili",   "Vili1234!",  "vili",   "1344444444",  "上海市浦东新区", "vili@cakeshop.com",  "0"},
                {"rider1", "Rider1234!", "张三",   "13900139000", "配送站",        "rider1@cakeshop.com", "2"}
            };

            try (Connection conn = dataSource.getConnection()) {
                // 2a) 升级旧 MD5 密码为 BCrypt
                try (java.sql.PreparedStatement ps = conn.prepareStatement(
                         "UPDATE user SET password = ? WHERE LENGTH(password) = 32 AND password REGEXP '^[0-9a-f]{32}$' AND username = ?")) {
                    for (String[] s : seedUsers) {
                        ps.setString(1, encoder.encode(s[1]));
                        ps.setString(2, s[0]);
                        if (ps.executeUpdate() > 0) System.out.println("Upgraded password for: " + s[0]);
                    }
                } catch (Exception ignored) {}

                // 确保 admin 为超级管理员
                try (java.sql.Statement st = conn.createStatement()) {
                    st.executeUpdate("UPDATE user SET admin_role = 'super_admin' WHERE username = 'admin' AND (admin_role IS NULL OR admin_role <> 'super_admin')");
                } catch (Exception ignored) {}

                // 2b) 重置非 BCrypt 密码的种子用户
                try (java.sql.PreparedStatement ps = conn.prepareStatement(
                         "UPDATE user SET password = ?, name = ?, phone = ?, address = ?, email = ?, isadmin = ? WHERE username = ? AND (password NOT LIKE '$2a$%' OR password IS NULL)")) {
                    for (String[] row : seedUsers) {
                        ps.setString(1, encoder.encode(row[1]));
                        ps.setString(2, row[2]);
                        ps.setString(3, row[3]);
                        ps.setString(4, row[4]);
                        ps.setString(5, row[5]);
                        ps.setString(6, row[6]);
                        ps.setString(7, row[0]);
                        if (ps.executeUpdate() > 0) System.out.println("Fixed non-BCrypt password for: " + row[0]);
                    }
                } catch (Exception ignored) {}

                // 2c) 确保种子用户存在（先查后插，兼容 H2 和 MySQL）
                try (java.sql.Statement checkStmt = conn.createStatement()) {
                    for (String[] row : seedUsers) {
                        java.sql.ResultSet rs = checkStmt.executeQuery(
                            "SELECT COUNT(*) FROM user WHERE username = '" + row[0].replace("'", "''") + "'");
                        rs.next();
                        if (rs.getInt(1) == 0) {
                            try (java.sql.PreparedStatement ps = conn.prepareStatement(
                                     "INSERT INTO user (username, password, name, phone, address, email, isadmin, admin_role) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                                ps.setString(1, row[0]);
                                ps.setString(2, encoder.encode(row[1]));
                                ps.setString(3, row[2]);
                                ps.setString(4, row[3]);
                                ps.setString(5, row[4]);
                                ps.setString(6, row[5]);
                                ps.setString(7, row[6]);
                                ps.setString(8, "admin".equals(row[0]) ? "super_admin" : null);
                                ps.executeUpdate();
                                System.out.println("Inserted missing seed user: " + row[0]);
                            }
                        }
                        rs.close();
                    }
                } catch (Exception ignored) {}

                // 2d) 升级其他 MD5 用户
                try (java.sql.PreparedStatement ps = conn.prepareStatement(
                         "UPDATE user SET password = ? WHERE LENGTH(password) = 32 AND password REGEXP '^[0-9a-f]{32}$' AND username NOT IN ('admin','vili','rider1')")) {
                    ps.setString(1, encoder.encode("Test1234!"));
                    int other = ps.executeUpdate();
                    if (other > 0) System.out.println("Upgraded " + other + " additional MD5 passwords");
                } catch (Exception ignored) {}
            } catch (Exception e) {
                System.out.println("User upgrade failed: " + e.getMessage());
            }
        }
        seedMessages();
    }

    /** Seed sample messages for the rider. */
    private void seedMessages() {
        // Use plain INSERT with try-catch for idempotency (H2 compatible)
        String insertSql = "INSERT INTO `rider_message` (`rider_id`, `type`, `title`, `content`, `create_time`) VALUES "
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
        // 用不过滤 status 的查询，确保能正确检测到已存在的商品
        List<Goods> goods = goodsMapper.getGoodsByPageAdmin(0, 1);
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
        goods.setStatus(1);
        goods.setTypeId(typeId);
        return goods;
    }
}
