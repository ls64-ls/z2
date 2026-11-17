# -*- coding: utf-8 -*-
"""把本地 SQLite 数据导出为 MySQL 初始化脚本（结构与数据）。

输出：docker/mysql/init/01-schema.sql、02-data.sql
时间字段会整体平移，使种子数据中的“今天”始终等于运行当天。
"""
import os
import re
import sqlite3
from datetime import date, datetime, timedelta

BASE = os.path.dirname(os.path.abspath(__file__))
INIT_DIR = os.path.join(BASE, 'init')
PROJECT = os.path.dirname(os.path.dirname(BASE))  # b2c

SHOP_DB = os.path.join(PROJECT, 'shop', 'b2cmall.db')
EMP_DB = os.path.join(PROJECT, 'employee', 'b2cmall.db')

DDL = {
    'b2cmall_shop': [
        """CREATE TABLE tb_shop (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  owner_name VARCHAR(64) DEFAULT NULL,
  phone VARCHAR(32) DEFAULT NULL,
  address VARCHAR(255) DEFAULT NULL,
  status INT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;""",
        """CREATE TABLE tb_product (
  id BIGINT NOT NULL AUTO_INCREMENT,
  shop_id BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  category VARCHAR(64) NOT NULL,
  price DECIMAL(12,2) NOT NULL DEFAULT 0,
  stock INT DEFAULT 0,
  type INT NOT NULL DEFAULT 1,
  description TEXT,
  status INT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  market_price DECIMAL(12,2) DEFAULT 0,
  sale_price DECIMAL(12,2) DEFAULT 0,
  sales INT DEFAULT 0,
  fake_sales INT DEFAULT 0,
  warehouse_stock INT DEFAULT 0,
  store_stock INT DEFAULT 0,
  delivery_types VARCHAR(255) DEFAULT '快递配送',
  sort INT DEFAULT 0,
  product_type VARCHAR(64) DEFAULT '普通商品',
  stock_threshold INT DEFAULT 10,
  cover_image VARCHAR(512) DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_product_shop (shop_id, status),
  KEY idx_product_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;""",
        """CREATE TABLE tb_order (
  id BIGINT NOT NULL AUTO_INCREMENT,
  shop_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  product_name VARCHAR(255) NOT NULL,
  amount DECIMAL(12,2) NOT NULL DEFAULT 0,
  quantity INT DEFAULT 1,
  status VARCHAR(32) NOT NULL DEFAULT 'CREATED',
  buyer_name VARCHAR(64) DEFAULT NULL,
  buyer_phone VARCHAR(32) DEFAULT NULL,
  buyer_address VARCHAR(255) DEFAULT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  order_no VARCHAR(64) DEFAULT NULL,
  order_type VARCHAR(32) DEFAULT '普通订单',
  order_category VARCHAR(32) DEFAULT '普通订单',
  pay_type VARCHAR(32) DEFAULT NULL,
  after_sale_status VARCHAR(32) DEFAULT '暂无售后',
  logistics_type VARCHAR(32) DEFAULT NULL,
  store_name VARCHAR(128) DEFAULT NULL,
  delivery_no VARCHAR(64) DEFAULT NULL,
  user_id BIGINT DEFAULT NULL,
  buyer_nick VARCHAR(64) DEFAULT NULL,
  product_image VARCHAR(512) DEFAULT NULL,
  paid_at DATETIME DEFAULT NULL,
  shipped_at DATETIME DEFAULT NULL,
  completed_at DATETIME DEFAULT NULL,
  deleted INT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_order_shop (shop_id, status, deleted),
  KEY idx_order_user (user_id, deleted),
  KEY idx_order_no (order_no),
  KEY idx_order_paid (paid_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;""",
        """CREATE TABLE tb_order_item (
  id BIGINT NOT NULL AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  product_id BIGINT DEFAULT NULL,
  product_name VARCHAR(255) DEFAULT NULL,
  product_image VARCHAR(512) DEFAULT NULL,
  unit_price DECIMAL(12,2) DEFAULT 0,
  quantity INT DEFAULT 1,
  is_gift INT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_item_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;""",
        """CREATE TABLE tb_refund (
  id BIGINT NOT NULL AUTO_INCREMENT,
  order_id BIGINT DEFAULT NULL,
  order_no VARCHAR(64) DEFAULT NULL,
  refund_no VARCHAR(64) DEFAULT NULL,
  amount DECIMAL(12,2) DEFAULT 0,
  reason VARCHAR(255) DEFAULT NULL,
  status VARCHAR(32) DEFAULT '退款申请中',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  handled_at DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_refund_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;""",
        """CREATE TABLE tb_message (
  id BIGINT NOT NULL AUTO_INCREMENT,
  shop_id BIGINT NOT NULL,
  title VARCHAR(255) NOT NULL,
  content VARCHAR(1024) DEFAULT NULL,
  is_read INT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  sender_id BIGINT DEFAULT NULL,
  order_id BIGINT DEFAULT NULL,
  order_no VARCHAR(64) DEFAULT NULL,
  msg_type INT DEFAULT 1,
  read_time DATETIME DEFAULT NULL,
  updated_at DATETIME DEFAULT NULL,
  PRIMARY KEY (id),
  KEY idx_message_shop (shop_id, is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;""",
    ],
    'b2cmall_employee': [
        """CREATE TABLE tb_employee (
  id BIGINT NOT NULL AUTO_INCREMENT,
  shop_id BIGINT NOT NULL,
  username VARCHAR(64) NOT NULL,
  password VARCHAR(128) NOT NULL,
  avatar_url VARCHAR(512) DEFAULT NULL,
  last_login_time DATETIME DEFAULT NULL,
  login_count INT DEFAULT 0,
  status INT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_employee (shop_id, username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;""",
        """CREATE TABLE tb_login_logs (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  login_ip VARCHAR(64) DEFAULT NULL,
  login_device VARCHAR(128) DEFAULT NULL,
  login_location VARCHAR(128) DEFAULT NULL,
  login_time DATETIME DEFAULT NULL,
  user_agent VARCHAR(512) DEFAULT NULL,
  status INT DEFAULT 0,
  PRIMARY KEY (id),
  KEY idx_log_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;""",
    ],
}

DATETIME_COLS = ('created_at', 'updated_at', 'paid_at', 'shipped_at', 'completed_at',
                 'handled_at', 'read_time', 'last_login_time', 'login_time')
TS_RE = re.compile(r'^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$')


def shift_days(db_path):
    """根据数据中最晚的支付日期计算需要平移的天数，使种子数据“今天”为运行当日。"""
    conn = sqlite3.connect(db_path)
    try:
        row = conn.execute("SELECT MAX(paid_at) FROM tb_order").fetchone()
    except sqlite3.Error:
        row = None
    conn.close()
    if not row or not row[0]:
        return 0
    ref = datetime.strptime(row[0][:10], '%Y-%m-%d').date()
    return (date.today() - ref).days


def esc(v):
    if v is None:
        return 'NULL'
    if isinstance(v, (int, float)):
        return str(v)
    return "'" + str(v).replace('\\', '\\\\').replace("'", "''") + "'"


def transform(col, value, delta):
    if value is None:
        return None
    if col in DATETIME_COLS and isinstance(value, str) and TS_RE.match(value) and delta:
        return (datetime.strptime(value, '%Y-%m-%d %H:%M:%S') + timedelta(days=delta)) \
            .strftime('%Y-%m-%d %H:%M:%S')
    return value


def dump(db_path, db_name, delta):
    conn = sqlite3.connect(db_path)
    conn.row_factory = sqlite3.Row
    out = []
    tables = [r[0] for r in conn.execute(
        "SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' ORDER BY name")]
    for t in tables:
        rows = conn.execute('SELECT * FROM "%s"' % t).fetchall()
        if not rows:
            continue
        cols = rows[0].keys()
        out.append('-- %s : %d 行' % (t, len(rows)))
        for r in rows:
            vals = [esc(transform(c, r[c], delta)) for c in cols]
            out.append('INSERT INTO %s (%s) VALUES (%s);' % (t, ', '.join(cols), ', '.join(vals)))
        out.append('')
    conn.close()
    return '\n'.join(out)


def main():
    os.makedirs(INIT_DIR, exist_ok=True)
    delta = shift_days(SHOP_DB)

    schema = ['SET NAMES utf8mb4;', 'SET FOREIGN_KEY_CHECKS = 0;', '']
    for db, stmts in DDL.items():
        schema.append('CREATE DATABASE IF NOT EXISTS %s DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;' % db)
        schema.append('USE %s;' % db)
        schema.extend(stmts)
        schema.append('')
    schema.append('SET FOREIGN_KEY_CHECKS = 1;')
    with open(os.path.join(INIT_DIR, '01-schema.sql'), 'w', encoding='utf-8') as f:
        f.write('\n'.join(schema))

    data = ['SET NAMES utf8mb4;', '']
    data.append('USE b2cmall_shop;')
    data.append(dump(SHOP_DB, 'b2cmall_shop', delta))
    data.append('USE b2cmall_employee;')
    data.append(dump(EMP_DB, 'b2cmall_employee', delta))
    with open(os.path.join(INIT_DIR, '02-data.sql'), 'w', encoding='utf-8') as f:
        f.write('\n'.join(data))

    print('delta days =', delta)
    print('written to', INIT_DIR)


if __name__ == '__main__':
    main()
