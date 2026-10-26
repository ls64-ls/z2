# -*- coding: utf-8 -*-
"""升级 shop 数据库结构并填充演示数据（需求 v1.1）"""
import sqlite3
import random
from datetime import datetime, timedelta
from urllib.parse import quote

DB = r'e:\XM1\b2c\shop\b2cmall.db'
conn = sqlite3.connect(DB)
cur = conn.cursor()


def add_column(table, column, ddl):
    cols = [r[1] for r in cur.execute('PRAGMA table_info(%s)' % table)]
    if column not in cols:
        cur.execute('ALTER TABLE %s ADD COLUMN %s %s' % (table, column, ddl))
        print('  + %s.%s' % (table, column))


print('== 扩展表结构 ==')
for c, d in [
    ('market_price', 'REAL DEFAULT 0'),
    ('sale_price', 'REAL DEFAULT 0'),
    ('sales', 'INTEGER DEFAULT 0'),
    ('fake_sales', 'INTEGER DEFAULT 0'),
    ('warehouse_stock', 'INTEGER DEFAULT 0'),
    ('store_stock', 'INTEGER DEFAULT 0'),
    ('delivery_types', "TEXT DEFAULT '快递配送'"),
    ('sort', 'INTEGER DEFAULT 0'),
    ('product_type', "TEXT DEFAULT '普通商品'"),
    ('stock_threshold', 'INTEGER DEFAULT 10'),
    ('cover_image', 'TEXT'),
]:
    add_column('tb_product', c, d)

for c, d in [
    ('order_no', 'TEXT'),
    ('order_type', "TEXT DEFAULT '普通订单'"),
    ('order_category', "TEXT DEFAULT '普通订单'"),
    ('pay_type', 'TEXT'),
    ('after_sale_status', "TEXT DEFAULT '暂无售后'"),
    ('logistics_type', 'TEXT'),
    ('store_name', 'TEXT'),
    ('delivery_no', 'TEXT'),
    ('user_id', 'INTEGER'),
    ('buyer_nick', 'TEXT'),
    ('product_image', 'TEXT'),
    ('paid_at', 'TEXT'),
    ('shipped_at', 'TEXT'),
    ('completed_at', 'TEXT'),
    ('deleted', 'INTEGER DEFAULT 0'),
]:
    add_column('tb_order', c, d)

for c, d in [
    ('sender_id', 'INTEGER'),
    ('order_id', 'INTEGER'),
    ('order_no', 'TEXT'),
    ('msg_type', 'INTEGER DEFAULT 1'),
    ('read_time', 'TEXT'),
    ('updated_at', 'TEXT'),
]:
    add_column('tb_message', c, d)

cur.executescript("""
CREATE TABLE IF NOT EXISTS tb_order_item (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER NOT NULL,
    product_id INTEGER,
    product_name TEXT,
    product_image TEXT,
    unit_price REAL DEFAULT 0,
    quantity INTEGER DEFAULT 1,
    is_gift INTEGER DEFAULT 0
);
CREATE TABLE IF NOT EXISTS tb_refund (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id INTEGER,
    order_no TEXT,
    refund_no TEXT,
    amount REAL DEFAULT 0,
    reason TEXT,
    status TEXT DEFAULT '退款申请中',
    created_at TEXT DEFAULT (datetime('now', 'localtime')),
    handled_at TEXT
);
""")

print('== 清理旧数据 ==')
cur.executescript("""
DELETE FROM tb_product;
DELETE FROM tb_order;
DELETE FROM tb_order_item;
DELETE FROM tb_message;
DELETE FROM tb_refund;
DELETE FROM sqlite_sequence WHERE name IN ('tb_product','tb_order','tb_order_item','tb_message','tb_refund');
""")

print('== 写入商品 ==')
now = datetime.now()


def img(name):
    return 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=%s&image_size=square' % quote(
        '电商商品图，纯白背景，' + name)


products = [
    # name, category, market, sale, sales, fake, wh, store, threshold, type, status, product_type, delivery, sort, desc
    ('无线蓝牙耳机 Pro 主动降噪', '数码', 399, 299, 1240, 5000, 40, 12, 20, 1, 1, '普通商品', '快递配送', 100, '蓝牙5.3，主动降噪，续航30小时'),
    ('机械键盘 87键 青轴 RGB', '数码', 599, 499, 860, 2000, 5, 3, 20, 1, 1, '普通商品', '快递配送,用户自提', 90, '87键热插拔，RGB背光'),
    ('4K 高清投影仪 家用', '数码', 2999, 2499, 120, 300, 30, 6, 15, 1, 1, '秒杀商品', '快递配送,同城配送', 80, '4K分辨率，自动对焦，梯形校正'),
    ('纯棉短袖T恤 男女同款', '服装', 129, 89, 3200, 8000, 200, 50, 50, 1, 1, '普通商品', '快递配送,同城配送,用户自提', 70, '100%纯棉，透气舒适'),
    ('时尚牛仔外套 春秋新款', '服装', 399, 319, 660, 1500, 80, 20, 40, 1, 1, '普通商品', '快递配送', 60, '水洗牛仔，宽松版型'),
    ('云南小粒咖啡豆 250g', '食品', 88, 68, 4500, 9000, 300, 60, 100, 1, 1, '普通商品', '快递配送,同城配送', 50, '中度烘焙，现磨手冲'),
    ('每日坚果礼盒 30包', '食品', 158, 128, 2100, 5000, 20, 5, 30, 1, 1, '普通商品', '快递配送,用户自提', 40, '混合坚果，独立小包装'),
    ('北欧风落地灯 客厅氛围', '家居', 699, 559, 320, 800, 60, 10, 20, 1, 1, '普通商品', '同城配送,用户自提', 30, '三色调光，简约北欧设计'),
    ('记忆棉护颈枕 慢回弹', '家居', 199, 149, 1500, 3000, 120, 30, 40, 1, 1, '普通商品', '快递配送,同城配送', 20, '慢回弹记忆棉，护颈助眠'),
    ('家用净水器滤芯 通用款', '家居', 259, 199, 900, 2000, 3, 1, 10, 1, 0, '普通商品', '快递配送', 10, '通用滤芯，适配多品牌净水器'),
    ('在线英语课程年卡', '虚拟服务', 1999, 1499, 210, 500, 0, 0, 0, 2, 1, '普通商品', '用户自提', 95, '一年有效期，外教一对一直播课，支持售后'),
    ('视频网站会员季卡', '数字商品', 78, 58, 5600, 12000, 0, 0, 0, 2, 1, '秒杀商品', '用户自提', 85, '3个月有效期，下单自动发货，支持售后'),
    ('电商礼品卡 100元电子卡券', '数字商品', 100, 98, 3000, 0, 0, 0, 0, 2, 1, '普通商品', '用户自提', 75, '电子卡券，有效期12个月，不可退换'),
    ('智能运动手环 心率监测', '数码', 299, 229, 1800, 3000, 90, 25, 50, 1, 1, '普通商品', '快递配送,同城配送', 65, '心率血氧监测，14天续航'),
    ('多功能空气炸锅 5L', '家居', 499, 399, 760, 1600, 45, 8, 30, 1, 1, '普通商品', '快递配送,同城配送,用户自提', 55, '5L大容量，可视窗口'),
]

for i, p in enumerate(products, start=1):
    cur.execute("""INSERT INTO tb_product
        (id, shop_id, name, category, price, stock, type, description, status, created_at,
         market_price, sale_price, sales, fake_sales, warehouse_stock, store_stock,
         delivery_types, sort, product_type, stock_threshold, cover_image)
        VALUES (?,3,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)""",
                (i, p[0], p[1], p[3], p[7] + p[8], p[9], p[14], p[10],
                 (now - timedelta(days=30 - i)).strftime('%Y-%m-%d %H:%M:%S'),
                 p[2], p[3], p[4], p[5], p[6], p[7], p[12], p[13], p[11], p[8], img(p[0])))

print('== 生成订单 ==')

pay_types = ['微信支付', '支付宝', '余额支付']
stores = ['杭州西湖旗舰店', '上海徐汇门店', '线上总店']
buyers = [('张伟', '13812345678', '浙江省杭州市西湖区文三路100号'),
          ('李娜', '13987654321', '上海市徐汇区漕溪北路56号'),
          ('王强', '13700001111', '广东省深圳市南山区科技园'),
          ('赵敏', '13622223333', '江苏省南京市鼓楼区中山路8号'),
          ('陈晨', '13544445555', '四川省成都市武侯区天府大道'),
          ('刘洋', '13466667777', '北京市朝阳区建国路88号')]

order_seq = [0]


def make_order(status, user_id, paid_dt, created_dt, complete_dt=None, ship_dt=None,
               pay_type=None, logistics='普通快递', store='线上总店', after_sale='暂无售后',
               item_indexes=None, order_type='普通订单', order_category='普通订单'):
    order_seq[0] += 1
    no = created_dt.strftime('%Y%m%d') + '%06d' % (1000 + order_seq[0])
    items = []
    total = 0.0
    qty_total = 0
    for idx, qty in item_indexes:
        p = products[idx - 1]
        price = p[3]
        items.append((idx, p[0], price, qty, img(p[0])))
        total += price * qty
        qty_total += qty
    first = items[0]
    buyer = buyers[user_id % len(buyers)]
    nick = 'user_%s' % user_id
    cur.execute("""INSERT INTO tb_order
        (shop_id, product_id, product_name, amount, quantity, status, buyer_name, buyer_phone, buyer_address,
         created_at, order_no, order_type, order_category, pay_type, after_sale_status, logistics_type,
         store_name, delivery_no, user_id, buyer_nick, product_image, paid_at, shipped_at, completed_at, deleted)
        VALUES (3,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,0)""",
                (first[0], first[1], round(total, 2), qty_total, status, buyer[0], buyer[1], buyer[2],
                 created_dt.strftime('%Y-%m-%d %H:%M:%S'), no, order_type, order_category, pay_type,
                 after_sale, logistics, store,
                 ('SF%013d' % random.randint(1000000, 9999999)) if status in ('SENT', 'COMPLETED') else None,
                 user_id, nick, first[4],
                 paid_dt.strftime('%Y-%m-%d %H:%M:%S') if paid_dt else None,
                 ship_dt.strftime('%Y-%m-%d %H:%M:%S') if ship_dt else None,
                 complete_dt.strftime('%Y-%m-%d %H:%M:%S') if complete_dt else None))
    oid = cur.lastrowid
    for it in items:
        cur.execute("""INSERT INTO tb_order_item (order_id, product_id, product_name, product_image, unit_price, quantity, is_gift)
                       VALUES (?,?,?,?,?,?,0)""", (oid, it[0], it[1], it[4], it[2], it[3]))
    return oid, no, total, buyer[0]


today0 = now.replace(hour=0, minute=0, second=0, microsecond=0)
orders_created = []

# 1) 待付款 0 单

# 2) 待发货 12 单：8 单今日支付，4 单昨日支付；其中 user_1001 占 9 单
today_hours = [9, 10, 11, 14, 15, 16, 20, 21]
yesterday_hours = [10, 13, 17, 21]
paid_today = []
for i in range(8):
    dt = today0 + timedelta(hours=today_hours[i], minutes=random.randint(0, 59))
    uid = 1001 if i < 6 else 1002 + i
    it = ([(i % 15) + 1, 1], [(i % 15) + 1, 2]) if i in (2, 5) else [((i % 15) + 1, 1)]
    if i == 2:
        it = [(1, 1), (4, 1)]
    orders_created.append(make_order('PAID', uid, dt, dt - timedelta(minutes=8),
                                     pay_type=pay_types[i % 3], store=stores[i % 3],
                                     item_indexes=it))
for i in range(4):
    dt = today0 - timedelta(days=1) + timedelta(hours=yesterday_hours[i], minutes=random.randint(0, 59))
    uid = 1001 if i < 3 else 1005
    orders_created.append(make_order('PAID', uid, dt, dt - timedelta(minutes=5),
                                     pay_type=pay_types[i % 3], store=stores[i % 3],
                                     item_indexes=[((i + 3) % 15 + 1, 1)]))

# 3) 待收货 2 单
for i in range(2):
    paid = now - timedelta(days=2 + i, hours=3)
    ship = now - timedelta(days=1, hours=2)
    orders_created.append(make_order('SENT', 1006 + i, paid, paid - timedelta(minutes=10),
                                     ship_dt=ship, pay_type=pay_types[i], logistics='同城配送',
                                     store=stores[i], item_indexes=[((i * 3 + 2) % 15 + 1, 1)]))

# 4) 已完成 24 单：user_1001 占 10 单，其余 3 单今日/2 单昨日支付用于趋势
completed_today = 0
for i in range(24):
    uid = 1001 if i < 10 else 1003 + i
    if i < 3:
        paid = today0 + timedelta(hours=[12, 18, 22][i], minutes=20)
        complete = now - timedelta(minutes=30 * (i + 1))
        completed_today += 1
    elif i < 5:
        paid = today0 - timedelta(days=1) + timedelta(hours=[11, 19][i - 3], minutes=15)
        complete = now - timedelta(days=1, hours=1)
    else:
        paid = now - timedelta(days=3 + (i % 15), hours=5)
        complete = paid + timedelta(days=1, hours=2)
    ship = paid + timedelta(hours=6)
    orders_created.append(make_order('COMPLETED', uid, paid, paid - timedelta(minutes=12),
                                     complete_dt=complete, ship_dt=ship,
                                     pay_type=pay_types[i % 3], logistics='普通快递' if i % 2 else '同城配送',
                                     store=stores[i % 3],
                                     item_indexes=[((i % 15) + 1, 1)] if i != 7 else [(8, 2), (9, 1)]))

# 5) 已取消 / 交易失败：user_1001 占 2 单
for i in range(2):
    dt = now - timedelta(days=4 + i, hours=6)
    orders_created.append(make_order('CANCELLED', 1001, None, dt,
                                     pay_type=None, item_indexes=[((i + 6) % 15 + 1, 1)]))

print('  订单数:', order_seq[0])

# 退款售后 1 单
target = cur.execute("SELECT id, order_no, amount FROM tb_order WHERE status='PAID' AND user_id=1001 LIMIT 1").fetchone()
cur.execute("UPDATE tb_order SET after_sale_status='退款申请中' WHERE id=?", (target[0],))
cur.execute("""INSERT INTO tb_refund (order_id, order_no, refund_no, amount, reason, status, created_at)
               VALUES (?,?,?,?,?,?,?)""",
            (target[0], target[1], 'RF' + target[1], 89.0, '商品与描述不符，申请退款',
             '退款申请中', (now - timedelta(hours=2)).strftime('%Y-%m-%d %H:%M:%S')))

print('== 写入消息 ==')
msgs = [
    ('订单支付成功通知', '用户 user_1001 已支付成功，订单号 %s，请及时处理' % orders_created[0][1],
     orders_created[0][0], orders_created[0][1], 1, now - timedelta(minutes=12)),
    ('退款申请通知', '有买家发起退款，订单编号 %s，请在 7 天内处理' % target[1], target[0], target[1], 2,
     now - timedelta(hours=2)),
    ('确认收货通知', '订单 %s 用户 张伟 已经确认收货' % orders_created[-3][1], orders_created[-3][0],
     orders_created[-3][1], 3, now - timedelta(hours=5)),
    ('订单支付成功通知', '用户 user_1001 已支付成功，订单号 %s，请及时处理' % orders_created[1][1],
     orders_created[1][0], orders_created[1][1], 1, now - timedelta(hours=7)),
    ('确认收货通知', '订单 %s 用户 李娜 已经确认收货' % orders_created[-4][1], orders_created[-4][0],
     orders_created[-4][1], 3, now - timedelta(hours=9)),
]
for title, content, oid, ono, mtype, dt in msgs:
    cur.execute("""INSERT INTO tb_message (shop_id, sender_id, title, content, msg_type, is_read, order_id, order_no, created_at, updated_at)
                   VALUES (3, 0, ?, ?, ?, 0, ?, ?, ?, ?)""",
                (title, content, mtype, oid, ono, dt.strftime('%Y-%m-%d %H:%M:%S'),
                 dt.strftime('%Y-%m-%d %H:%M:%S')))

conn.commit()

print('== 校验 ==')
for label, sql in [
    ('待付款', "SELECT COUNT(*) FROM tb_order WHERE shop_id=3 AND status='CREATED' AND deleted=0"),
    ('待发货', "SELECT COUNT(*) FROM tb_order WHERE shop_id=3 AND status='PAID' AND deleted=0"),
    ('待收货', "SELECT COUNT(*) FROM tb_order WHERE shop_id=3 AND status='SENT' AND deleted=0"),
    ('已完成', "SELECT COUNT(*) FROM tb_order WHERE shop_id=3 AND status='COMPLETED' AND deleted=0"),
    ('售后', "SELECT COUNT(*) FROM tb_refund WHERE status='退款申请中'"),
    ('库存预警', "SELECT COUNT(*) FROM tb_product WHERE shop_id=3 AND (warehouse_stock+store_stock) < stock_threshold AND stock_threshold>0"),
    ('user1001待发货', "SELECT COUNT(*) FROM tb_order WHERE user_id=1001 AND status='PAID' AND deleted=0"),
    ('user1001已完成', "SELECT COUNT(*) FROM tb_order WHERE user_id=1001 AND status='COMPLETED' AND deleted=0"),
    ('今日支付订单', "SELECT COUNT(*) FROM tb_order WHERE date(paid_at)=date('now','localtime')"),
    ('今日支付金额', "SELECT IFNULL(SUM(amount),0) FROM tb_order WHERE date(paid_at)=date('now','localtime')"),
    ('昨日支付金额', "SELECT IFNULL(SUM(amount),0) FROM tb_order WHERE date(paid_at)=date('now','localtime','-1 day')"),
]:
    print('  %s: %s' % (label, cur.execute(sql).fetchone()[0]))

conn.close()
print('完成')
