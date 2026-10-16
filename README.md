# xx-forum 后端

一个基于 Spring Boot 3 的论坛（社区）后端服务，支持板块、帖子、回复、私信、签到与版主管理等完整社区功能。

## 技术栈

| 组件 | 版本 / 说明 |
| --- | --- |
| Spring Boot | 3.4.7（JDK 17） |
| MyBatis | mybatis-spring-boot-starter + PageHelper 分页 |
| MySQL | 业务库 `xx_forum`，Quartz 独立库 `xx_forum_quartz` |
| Druid | 双数据源连接池 |
| Redis | 缓存用户信息、热门板块、在线会话状态 |
| WebSocket | STOMP over SockJS，用于私信与系统通知推送 |
| Quartz | JDBC 持久化调度，每日 23:59 重置签到状态 |
| AOP | 自定义 `@AddExp` 注解实现签到经验值自增 |
| JWT | jjwt，登录态校验（HTTP 头 `token`） |
| 阿里云 OSS | 图片/文件上传 |

## 模块结构

Maven 多模块工程，依赖方向为 `server → pojo → common`：

```
xx-forum
├── xx-forum-common   通用层：Result/PageResult 统一返回、JWT/OSS/Redis 工具类、
│                     BaseContext 用户上下文、全局异常、枚举、JwtProperties
├── xx-forum-pojo     模型层：entity（数据库实体）、dto（入参）、dvo（出参视图对象）
└── xx-forum-server   应用层：controller / service / mapper / config / aop
```

`xx-forum-server` 内部分层：

- `controller/user` — 按业务拆分的 REST 接口（用户、帖子、板块、回复、私信、签到、个人信息）
- `service` + `service/Impl` — 业务逻辑
- `mapper` + `resources/com/cyj/xx/mapper/*.xml` — 数据访问，XML 手写 SQL
- `config` — 数据源、Redis、WebMvc（跨域+拦截器）、WebSocket、Quartz、时间格式化
- `interceptor/LoginCheckInterceptor` — JWT 登录校验
- `HandshakeHandler` / `WebSocketEventListener` — WebSocket 握手鉴权与上下线监听
- `Aop` — 经验值切面
- `QuatzJob` — 定时任务

## 架构要点

### 1. 统一返回与异常

所有接口返回 `Result<T>`（`code` / `msg` / `data`），分页返回 `PageResult`。业务异常
（`UserNotLoginException`、`PasswordErrorException`、`UserBannedException` 等）由
`GlobalExceptionHandler` 统一捕获并转换。

### 2. 鉴权流程

1. `POST /user/login` 校验密码（MD5 摘要），签发 JWT（载荷含 `Id`、`username`）。
2. 前端把 token 放在请求头 `token` 中。
3. `LoginCheckInterceptor` 拦截 `/user/**`，解析成功则把 userId 写入 `BaseContext`（ThreadLocal）
   供 service 层读取；部分浏览类接口（帖子列表、板块详情、搜索、回复列表、注册登录）在
   `WebMvcConfiguration` 中放行。
4. WebSocket 单独走 `WebSocketHandshakeInterceptor`：从握手 URL 的 `token` 参数解析 JWT，
   写入会话属性 `USER_ID`，再由 `UserIdHandshakeHandler` 转成 `UserIdPrincipal` 作为 Principal。

### 3. 实时通信（私信 / 通知）

- 端点 `/ws`，广播前缀 `/topic`、`/queue`，点对点前缀 `/user`，客户端发送前缀 `/app`。
- 私信：客户端 `SEND /app/private-message` → `MessageController` 落库（`PrivateMessage`）→
  用 `SimpMessagingTemplate` 推送到 `/user/{userId}/queue/messages`。
- 在线状态：`SessionConnectedEvent` / `SessionDisconnectEvent` 监听器在 Redis 写入/删除
  `user_Session:{userId}`，推送前先判断接收方是否在线。
- 互动通知：点赞、回复等行为写入 `InteractionNotification`，支持未读、批量已读。

### 4. 板块与版主治理

- 板块（`Section`）含版主（`adminId`）、关注数、帖子数，支持关注/取关、创建与修改。
- 版主可在**自己板块范围内**封禁用户或帖子：`BannedUser` / `BannedPost` 均带 `sectionId`，
  即封禁是板块级而非全站级。
- 所有版主操作（封禁、解封、置顶、取消置顶）记入 `ModeratorOperationLog`，
  操作类型/目标类型用 `OperationType`、`TargetType` 枚举表达，前端可通过日志接口查询。

### 5. 签到与经验值

- `UserSignIn` 记录用户在板块下的签到状态、连续天数、经验值。
- 签到逻辑上标注 `@AddExp`，`GetExpAop` 在方法执行后自动 +5 经验。
- `QuartzConfig` 注册 `UserSignInJob`（cron `0 59 23 * * ?`），每天 23:59 批量把签到状态
  重置为未签到；任务与触发器持久化在 `xx_forum_quartz` 库中。

### 6. 缓存

- 登录后把用户概要写入 Redis Hash `user:{id}`（TTL 1 天），减少重复查询。
- 热门板块结果缓存在 `Sec:hot`。
- 统一封装在 `RedisUtil` / `RedisConfig`（自定义序列化）。

## 主要功能与接口

| 领域 | 代表接口 |
| --- | --- |
| 用户 | `POST /user/login`、`POST /user/enroll`、`POST /user/update`、`POST /user/changepwd`、`GET /user/personal`、`DELETE /user/quit` |
| 帖子 | `GET /user/posts`、`GET /user/postdetail`、`POST /user/pushPost`、`POST /user/likePost/{id}`、`POST /user/collectPost/{id}`、`GET /user/post/search` |
| 回复 | `GET /user/reply`、`POST /user/newReply`、`POST /user/doToggleLike/{postId}/{replyId}` |
| 板块 | `GET /user/sections/hot`、`GET /user/sections/sectionDetail`、`POST /user/sections/createSection`、`POST /user/sections/followSec/{id}` |
| 版主治理 | `POST /user/post/confirmBanPost`、`PUT /user/post/pinPost/{id}`、`POST /user/sections/confirmBanUser`、封禁/操作日志查询 |
| 私信与通知 | `GET /user/chat/private/Contacts`、`GET /user/chat/private/Message`、`POST /user/chat/private/markAsRead`、`GET /user/chat/system/Notifications`，WS `SEND /app/private-message` |
| 个人中心 | `GET /user/myInformation`、`/myPost`、`/myReply`、`/myCollection`、`/myFollow`、`/manageSections` |
| 签到 | `GET /user/SignIn/{id}` |
| 上传 | `POST /user/upload`（阿里云 OSS） |

：`InteractionNotification`、`PrivateMessage`、`UserSignIn`

## 本地运行

服务默认监听 `8080`，前端通过 `http://localhost:8080/user/**` 访问；跨域已在
`WebMvcConfiguration` 中放开。

