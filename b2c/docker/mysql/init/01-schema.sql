SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS b2cmall_shop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE b2cmall_shop;
CREATE TABLE tb_shop (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  owner_name VARCHAR(64) DEFAULT NULL,
  phone VARCHAR(32) DEFAULT NULL,
  address VARCHAR(255) DEFAULT NULL,
  status INT DEFAULT 0,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE tb_product (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE tb_order (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE tb_order_item (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE tb_refund (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE tb_message (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE DATABASE IF NOT EXISTS b2cmall_employee DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE b2cmall_employee;
CREATE TABLE tb_employee (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE tb_login_logs (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;