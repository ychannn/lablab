# lablab - 跨文化信息分析与智能决策重点实验室网站

实验室门户网站 + 后台管理系统，采用前后端分离架构。

## 项目结构

```
lablab/
├── src/                    # 后端（Spring Boot 3.2.5 + Java 17）
│   └── main/java/org/ychan/lablab/
│       ├── controller/     # 接口层
│       ├── service/        # 业务层
│       ├── mapper/         # MyBatis-Plus 数据访问层
│       ├── entity/         # 数据库实体
│       ├── dto/            # 数据传输对象
│       ├── config/         # 配置（拦截器、CORS 等）
│       └── common/         # 公共工具类 & 常量
├── frontend/               # 门户前端（Vue 3 + Vite，端口 5173）
├── admin/                  # 后台管理前端（Vue 3 + Vite，端口 5174）
└── pom.xml                 # Maven 依赖配置
```

## 技术栈

| 模块  | 技术                                |
| --- | --------------------------------- |
| 后端  | Spring Boot 3.2.5, Java 17, Maven |
| ORM | MyBatis-Plus 3.5.5                |
| 数据库 | MySQL                             |
| 缓存  | Redis（Redisson）                   |
| 搜索  | 数据库搜索                             |
| 鉴权  | JWT + BCrypt                      |
| 邮件  | Spring Mail（SMTP）                 |
| 前端  | Vue 3, Vite 5                     |
| 富文本 | wangEditor (仅 admin)              |

## 环境准备

启动项目前，确保本地已安装以下服务并正常运行：

- **JDK 17+**
- **Maven 3.6+**
- **Node.js 16+**（前端需要）
- **MySQL**（默认端口 3306）
- **Redis**（默认 localhost:6379，Redisson 自动连接）

## 快速启动

### 1. 初始化数据库

在 MySQL 中创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS lab DEFAULT CHARACTER SET utf8mb4;
```

项目使用 MyBatis-Plus，启动时会自动建表。如需手动执行增量 SQL，可查看 `src/main/resources/db/` 目录下的脚本。

### 2. 配置邮件（可选，不影响启动）

如需邮件验证码功能：

1. 复制 `mail-credentials.properties.example` 为 `mail-credentials.properties`
2. 填写真实的邮箱账号和 SMTP 授权码

```
mail.username=your@163.com
mail.password=your-smtp-auth-code
```

不配置的话验证码会直接打印在后端控制台，方便开发调试。

### 3. 启动后端

```bash
# 在项目根目录下执行
mvn spring-boot:run
```

后端默认运行在 **8080** 端口。首次启动会自动创建默认管理员账号：

> 用户名：**admin**，密码：**admin123**

### 4. 启动前端

#### 门户网站（端口 5173）

```bash
cd frontend
npm install
npm run dev
```

访问 <http://localhost:5173>

#### 后台管理（端口 5174）

```bash
cd admin
npm install
npm run dev
```

访问 <http://localhost:5174/admin>

## 环境切换

`src/main/resources/application.yml` 中 `spring.profiles.active` 控制环境：

- **dev** — 开发环境，数据库连 localhost:4406
- **prod** — 生产环境，数据库连 localhost:3306

修改该值即可切换。两个环境对应的配置文件：

- `application-dev.yml`
- `application-prod.yml`

## 默认管理员

项目启动时会自动检查，如果没有 admin 账号则自动创建。

| 角色    | 用户名   | 密码       |
| ----- | ----- | -------- |
| 超级管理员 | admin | admin123 |

可以通过后台管理的"管理员管理"菜单新增其他管理员（仅超级管理员可见）。

## 项目结构补充说明

### 后端接口认证

接口请求需要在 Header 携带 JWT Token：

```
Authorization: Bearer <token>
```

登录接口、忘记密码接口等不需要鉴权（已在 `WebConfig.java` 中配置白名单）。

### 文件上传

上传文件存储在项目运行目录的 `uploads/` 文件夹下，通过 `/uploads/**` 路径访问。

生产环境建议使用绝对路径或挂载到 OSS/CDN。

### 前端 API 代理

- `admin` 前端开发时通过 Vite proxy 将 `/api` 和 `/uploads` 代理到 `http://localhost:8080`
- `frontend` 门户前端没有配置 proxy，API 地址通过 `VITE_API_BASE` 环境变量或直接使用 `/api` 路径
- 生产部署时通常由 Nginx 统一反向代理

## 常见问题

### 1. 启动后端报数据库连接失败

检查 MySQL 是否已启动，以及 `application-{profile}.yml` 中的数据库地址、用户名、密码是否正确。

开发环境默认连接 `localhost:4406`，如果你的 MySQL 端口是 3306，需要修改配置文件。

### 2. 启动报 Redis 连接失败

确保 Redis 服务已启动。Redisson 默认连接 `localhost:6379`，无需密码。

### 3. 页面接口请求 401

清除浏览器 localStorage 中的 `admin_token`，重新登录即可。

### 4. 端口被占用

- 后端：8080
- 门户前端：5173
- 后台管理前端：5174

如果端口被占用，后端在 `application-{profile}.yml` 中修改 `server.port`，前端在各自的 `vite.config.js` 中修改 `server.port`，同时需要同步修改 `WebConfig.java` 中的 CORS 允许地址。

## 生产部署注意事项

1. 修改 `application.yml` 中 profile 为 `prod`
2. 在生产环境配置文件中填入真实的数据库、Redis、邮件等配置
3. 前端执行 `npm run build` 构建，产物分别在 `frontend/dist/` 和 `admin/dist/`
4. 建议使用 **Nginx** 做反向代理，统一处理静态文件、API 代理和上传文件访问
5. `mail-credentials.properties` 不要提交到 Git（已在 `.gitignore` 中）
6. 建议修改默认管理员密码

