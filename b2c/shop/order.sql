CREATE TABLE IF NOT EXISTS tb_order (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    shop_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    product_name TEXT NOT NULL,
    amount REAL NOT NULL,
    quantity INTEGER DEFAULT 1,
    status INTEGER DEFAULT 0,
    buyer_name TEXT,
    buyer_phone TEXT,
    buyer_address TEXT,
    created_at TEXT DEFAULT (datetime('now', 'localtime'))
);

-- 插入测试订单数据
INSERT INTO tb_order (shop_id, product_id, product_name, amount, quantity, status, buyer_name, buyer_phone, buyer_address)
VALUES
(3, 1, '纯棉T恤', 99.99, 2, 1, '张三', '13800138001', '北京市朝阳区xxx街道'),
(3, 1, '纯棉T恤', 99.99, 1, 2, '李四', '13800138002', '上海市浦东新区xxx路'),
(3, 2, '视频会员月卡', 29.9, 1, 3, '王五', '13800138003', '广州市天河区xxx大道'),
(3, 1, '纯棉T恤', 99.99, 3, 0, '赵六', '13800138004', '深圳市南山区xxx街'),
(3, 2, '视频会员月卡', 29.9, 2, 1, '钱七', '13800138005', '杭州市西湖区xxx路');
