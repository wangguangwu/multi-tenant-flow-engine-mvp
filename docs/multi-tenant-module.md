# 多租户模块设计与实现

## 1. 多租户的核心概念

多租户（Multi-tenancy）是一种软件架构设计模式，允许单个应用实例同时服务多个客户（租户），同时保证租户之间的数据隔离和安全性。在我们的流程编排引擎中，多租户架构具有以下特点：

### 1.1 租户隔离

- **数据隔离**：每个租户的数据相互独立，不会互相影响
- **资源隔离**：租户间的资源（如线程、内存等）相互隔离
- **配置隔离**：每个租户可以拥有自己的配置参数

### 1.2 租户上下文

- **租户上下文**：包含租户标识、租户名称等信息
- **上下文传播**：确保租户信息在系统各组件间正确传递
- **线程安全**：保证在多线程环境下的租户上下文安全

### 1.3 系统租户

- **系统租户**：特殊的租户身份，用于执行系统级操作
- **权限区分**：系统租户拥有更高的权限和访问能力

## 2. 项目结构

多租户模块采用清晰的分层结构，确保关注点分离和代码的可维护性：

```
flow-engine-tenant/
├── src/main/java/com/wangguangwu/flowengine/tenant/
│   ├── api/                  # 对外暴露的接口和注解
│   │   ├── TenantAware.java       # 租户感知注解
│   │   ├── TenantContext.java     # 租户上下文接口
│   │   ├── TenantContextInitializer.java  # 租户上下文初始化器接口
│   │   └── TenantRouting.java     # 租户路由注解
│   │
│   ├── core/                 # 核心实现类
│   │   ├── DefaultTenantContext.java    # 默认租户上下文实现
│   │   ├── TenantContextHolder.java     # 租户上下文持有者
│   │   └── TenantContextPropagator.java # 租户上下文传播器
│   │
│   ├── exception/            # 异常类
│   │   └── TenantNotFoundException.java # 租户未找到异常
│   │
│   └── spring/               # Spring集成
│       ├── TenantAutoConfiguration.java  # 自动配置类
│       ├── TenantAwareAspect.java        # 租户感知切面
│       ├── TenantAwareAsyncAspect.java   # 异步方法租户感知切面
│       ├── TenantContextFilter.java      # 租户上下文过滤器
│       └── TenantProperties.java         # 租户配置属性
```

## 3. 核心组件与实现

### 3.1 租户上下文（TenantContext）

租户上下文是多租户系统的核心概念，包含租户的基本信息：

- **接口定义**：`TenantContext` 定义了获取租户ID、租户名称和判断是否系统租户的方法
- **默认实现**：`DefaultTenantContext` 提供了租户上下文的标准实现
  - 使用 Java Record 实现不可变对象
  - 提供静态工厂方法创建普通租户和系统租户
  - 租户名称自动添加 "Tenant-" 前缀，增强可读性

### 3.2 租户上下文管理

- **TenantContextHolder**：
  - 使用 `InheritableThreadLocal` 存储租户上下文，确保线程安全
  - 支持父子线程间的租户上下文自动传递
  - 提供获取、设置、清理租户上下文的方法

- **TenantContextPropagator**：
  - 提供 `Runnable` 和 `Callable` 的包装，确保在异步执行时传递租户上下文
  - 适用于复杂的异步场景，如自定义线程池

### 3.3 租户感知注解

- **@TenantAware**：
  - 标记需要租户上下文的方法
  - 支持指定租户ID参数
  - 支持系统租户标记

- **@TenantRouting**：
  - 用于数据源路由的注解
  - 简化版实现，可根据需要扩展

### 3.4 Spring集成

- **TenantContextFilter**：
  - 从HTTP请求头中提取租户ID
  - 自动创建并设置租户上下文
  - 请求结束后清理租户上下文

- **TenantAwareAspect**：
  - 拦截带有 `@TenantAware` 注解的方法
  - 自动设置和清理租户上下文
  - 支持从方法参数中提取租户ID

- **TenantAwareAsyncAspect**：
  - 拦截带有 `@Async` 注解的方法
  - 确保异步方法执行时能正确传播租户上下文
  - 处理线程池重用线程的情况，避免上下文泄漏

- **TenantAutoConfiguration**：
  - 自动配置多租户功能的Spring Bean
  - 支持条件化配置，可通过配置启用/禁用
  - 提供默认实现，也支持自定义覆盖

### 3.5 异常处理

- **TenantNotFoundException**：
  - 当租户上下文未找到时抛出
  - 提供清晰的错误信息，便于排查问题

## 4. 使用方式

### 4.1 基本使用

```java
// 获取当前租户上下文
TenantContext context = TenantContextHolder.getContext();
String tenantId = context.tenantId();

// 手动设置租户上下文
TenantContextHolder.setContext(DefaultTenantContext.create("tenant-001"));

// 清理租户上下文
TenantContextHolder.clearContext();
```

### 4.2 注解使用

```java
// 使用租户感知注解
@TenantAware
public void processTenantData() {
    // 方法内可以安全访问租户上下文
    TenantContext context = TenantContextHolder.getContext();
    // 业务逻辑...
}

// 指定租户ID参数
@TenantAware(tenantIdParam = "tenantId")
public void processTenantData(String tenantId, String data) {
    // 方法执行前，会自动设置指定的租户上下文
    // 业务逻辑...
}

// 标记为系统租户方法
@TenantAware(systemTenant = true)
public void processSystemOperation() {
    // 方法执行时会使用系统租户上下文
    // 业务逻辑...
}

// 异步方法自动传播租户上下文
@Async
public void processAsync() {
    // 异步线程中可以安全访问租户上下文
    TenantContext context = TenantContextHolder.getContext();
    // 业务逻辑...
}
```

### 4.3 配置选项

在 `application.properties` 或 `application.yml` 中配置：

```yaml
flow:
  engine:
    tenant:
      enabled: true  # 启用多租户功能
      tenant-id-header: X-Tenant-ID  # 自定义租户ID请求头
      system-tenant-id: system  # 自定义系统租户ID
```

## 5. 最佳实践

1. **始终使用注解**：优先使用 `@TenantAware` 和 `@Async` 注解，减少手动管理租户上下文的代码
2. **请求入口处理**：在API网关或请求入口处设置租户上下文，确保整个调用链都能访问
3. **及时清理上下文**：在操作完成后清理租户上下文，避免内存泄漏
4. **异常处理**：妥善处理 `TenantNotFoundException` 异常，提供友好的错误信息
5. **避免静态变量**：不要使用静态变量存储租户相关的数据，可能导致租户数据混淆

## 6. 未来扩展

1. **数据源路由**：完善 `TenantRouting` 注解的实现，支持动态数据源切换
2. **缓存隔离**：实现基于租户的缓存隔离策略
3. **权限集成**：与权限系统集成，实现更细粒度的租户权限控制
4. **监控指标**：添加租户级别的监控指标，便于问题排查和性能优化
5. **租户生命周期管理**：实现租户的创建、更新、删除等管理功能
