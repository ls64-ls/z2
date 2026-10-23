[Uploading README.md…]()
# B2C 商城微服务系统

基于 Spring Boot + Spring Cloud Alibaba 的 B2C 电商微服务项目。

## 技术栈

| 技术 | 版本 |
|------|------|
| Java | 8 |
| Spring Boot | 2.7.18 |
| Spring Cloud | 2021.0.5 |
| Spring Cloud Alibaba | 2021.0.4.0 |
| MyBatis | 2.3.2 |
| SQLite | 3.42.0.0 |
| Nacos | 1.4.7 |
| Hutool | 5.8.38 |
| Apache POI | 5.2.3 |

## 模块说明

| 模块 | 端口 | 说明 |
|------|------|------|
| common | - | 公共工具类、统一响应封装 |
| gateway | 8088 | 统一网关，路由转发 |
| shop | 8091 | 店铺 / 商品管理 |
| employee | 8092 | 员工管理、登录日志 |
| order | 8083 | 订单管理（状态模式 + 策略模式） |

## 设计模式

### 状态模式（订单状态流转）

订单状态：`CREATED` → `PAID` → `SENT` → `COMPLETED`，`CANCELLED` 为终止态。

- `OrderStateService`：状态接口
- `OrderContext`：上下文，持有当前状态并委托调用
- 各状态实现类：`CreateOrderStateServiceImpl`、`PayStateServiceImpl`、`SentStateServiceImpl`、`CompletedStateServiceImpl`、`CancelledStateServiceImpl`

### 策略模式（支付方式）

支持 `ALIPAY`、`WECHAT`、`UNIONPAY` 三种支付渠道。

## 环境配置

- 注册中心：Nacos `47.100.22.158:8848`，命名空间 `zhushilu`
- 数据库：SQLite（`shop/b2cmall.db`、`employee/b2cmall.db`）
- 缓存：Redis `47.100.22.158:6379`

## 构建与运行

```bash
# 构建
mvn clean package -DskipTests

# 启动各服务（需指定 UTF-8 编码，避免中文乱码）
java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 \
     -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 \
     -jar order/target/order-1.0-SNAPSHOT.jar
```

依次启动 order、shop、employee、gateway 四个服务。

## 前端页面

前端为纯 HTML/CSS/JS（无构建依赖），位于 `frontend/`，直接以静态服务打开即可（接口跨域已在 shop 侧放开）。

```bash
# 推荐在 8010 端口提供静态服务
python -m http.server 8010 --directory frontend
# 浏览器访问 http://localhost:8010/index.html
```

| 页面 | 说明 |
|------|------|
| `index.html` | 商家后台登录（登录后写入 localStorage） |
| `dashboard.html` | 店铺 Dashboard：6 个状态卡片、24 小时交易双折线图、4 项同比指标、消息通知 |
| `product-manage.html` | 商品管理：多条件筛选、批量操作、Excel 导入导出、上下架、删除校验 |
| `order-manage.html` | 订单查询：11 类筛选、状态快捷标签、订单组展示、发货、退款信息 |
| `mall.html` | C 端商品分类列表：一级分类导航、销量/价格排序 |
| `product-detail.html` | C 端商品详情：大图、价格、库存、配送方式、立即购买 |
| `my-orders.html` | C 端我的订单：状态 Tab、订单号查询、详情、再次购买、软删除 |

> C 端无用户模块，演示账号固定为 `USER_ID=1001`、店铺 `SHOP_ID=3`（见 `frontend/assets/common.js`）。

## 主要接口（shop 服务，8091）

| 接口 | 说明 |
|------|------|
| `GET /dashboard?shopId=3` | Dashboard 卡片、趋势、指标、消息 |
| `POST /dashboard/message/read` | 标记消息已读 |
| `GET /product/page` | 商品分页筛选（名称/类型/类别/状态/配送/分类/库存预警） |
| `GET /product/detail`、`POST /product/update` | 商品详情 / 编辑 |
| `POST /product/status`、`POST /product/delete` | 上下架 / 删除（已产生订单禁止删除） |
| `POST /product/batch` | 批量设置 |
| `GET /product/options` | 筛选项下拉数据 |
| `GET /product/export`、`POST /product/import` | 商品 Excel 导出 / 导入 |
| `GET /product/mall/categories`、`/product/mall/list`、`/product/mall/detail` | C 端分类、列表、详情 |
| `GET /order/page`、`GET /order/detail` | 订单分页查询 / 详情 |
| `GET /order/status-count` | 状态标签计数 |
| `POST /order/ship`、`GET /order/export`、`GET /order/refund` | 发货 / 导出 / 退款信息 |
| `GET /c/order/list`、`/c/order/status-count`、`/c/order/detail` | C 端订单列表、Tab 计数、详情 |
| `POST /c/order/delete`、`GET /c/order/rebuy` | 软删除 / 再次购买信息 |

## 数据库

- `shop/b2cmall.db`：`tb_product`、`tb_order`、`tb_order_item`、`tb_refund`、`tb_message`
- 商品图片字段 `cover_image` 为外链；Excel 导入不会覆盖已有图片与描述


## 包结构

```
com.zsl.b2cmall
├── common      公共模块
├── gateway     网关
├── shop        店铺商品
├── employee    员工
└── order       订单
```
