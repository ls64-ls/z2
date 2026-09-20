[README.md](https://github.com/user-attachments/files/32430933/README.md)
I'll create a simple README for the B2C mall project. Let me first check the project structure to ensure accuracy.
The user wants me to create a simple README document for the project. Let me recall the relevant experience first, then write a README.md for the b2c project.

The project is a B2C mall microservice system built with Spring Boot. Key modules:
- common: common utilities, response wrappers
- gateway: API gateway (Spring Cloud Gateway)
- shop: shop/product management
- employee: employee management
- order: order management with state pattern + strategy pattern
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

## 包结构

```
com.zsl.b2cmall
├── common      公共模块
├── gateway     网关
├── shop        店铺商品
├── employee    员工
└── order       订单
```

Key technical details:
- Java 8, Spring Boot 2.7.18, Spring Cloud (Nacos)
- SQLite database
- MyBatis
- Package: com.zsl.b2cmall
- Nacos: 47.100.22.158:8848, namespace: zhushilu

Let me create a simple README.md.

Now I have enough information to write a README. Let me create a simple README.md for the b2c project.

Key info:
- B2C 商城微服务系统
- Tech stack: Spring Boot 2.7.18, Spring Cloud 2021.0.5, Spring Cloud Alibaba 2021.0.4.0, MyBatis, SQLite, Nacos, Lombok, Hutool, Guava
- Java 8
- Package: com.zsl.b2cmall
- Modules: common, gateway, shop, employee, order
- Ports: gateway 8088, shop 8091, employee 8092, order 8083
- Nacos: 47.100.22.158:8848, namespace zhushilu
- Design patterns: State pattern (order status), Strategy pattern (payment)
- Database: SQLite (b2cmall.db)

Let me write the README now.

README.md created successfully. Let me provide a summary to the user.
已创建 [README.md](file:///e:/XM1/b2c/README.md)，包含：

- 项目简介与技术栈版本表
- 5 个模块说明（common/gateway/shop/employee/order）及端口
- 设计模式说明（订单状态模式、支付策略模式）
- 环境配置（Nacos、SQLite、Redis）
- 构建与运行命令（含 UTF-8 编码参数，避免中文乱码）
- 包结构示意
