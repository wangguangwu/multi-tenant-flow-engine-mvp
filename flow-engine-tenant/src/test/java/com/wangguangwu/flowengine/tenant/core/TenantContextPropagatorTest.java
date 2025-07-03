package com.wangguangwu.flowengine.tenant.core;

import com.wangguangwu.flowengine.tenant.api.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TenantContextPropagator 单元测试
 *
 * @author wangguangwu
 */
class TenantContextPropagatorTest {

    private ExecutorService executorService;
    private final String testTenantId = "test-tenant";

    @BeforeEach
    void setUp() {
        executorService = Executors.newSingleThreadExecutor();
        TenantContext testContext = DefaultTenantContext.create(testTenantId);
        TenantContextHolder.setContext(testContext);
    }

    @AfterEach
    void tearDown() {
        TenantContextHolder.clearContext();
        executorService.shutdown();
    }

    @Test
    @DisplayName("测试包装Runnable并传播租户上下文")
    void testWrapRunnable() throws InterruptedException {
        // 准备测试数据
        AtomicReference<String> executedTenantId = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        
        // 创建原始Runnable
        Runnable originalRunnable = () -> {
            TenantContext context = TenantContextHolder.getContext();
            if (context != null) {
                executedTenantId.set(context.tenantId());
            }
            latch.countDown();
        };
        
        // 使用传播器包装Runnable
        Runnable wrappedRunnable = TenantContextPropagator.wrap(originalRunnable);
        
        // 在新线程中执行包装后的Runnable
        executorService.execute(wrappedRunnable);
        
        // 等待执行完成
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        
        // 验证结果
        assertEquals(testTenantId, executedTenantId.get(), "租户上下文应该被正确传播到执行线程");
    }
    
    @Test
    @DisplayName("测试包装Callable并传播租户上下文")
    void testWrapCallable() throws Exception {
        // 准备测试数据
        Callable<String> originalCallable = () -> {
            TenantContext context = TenantContextHolder.getContext();
            return context != null ? context.tenantId() : null;
        };
        
        // 使用传播器包装Callable
        Callable<String> wrappedCallable = TenantContextPropagator.wrap(originalCallable);
        
        // 在新线程中执行包装后的Callable
        Future<String> future = executorService.submit(wrappedCallable);
        
        // 获取结果并验证
        String result = future.get(1, TimeUnit.SECONDS);
        assertEquals(testTenantId, result, "租户上下文应该被正确传播到执行线程");
    }
    
    @Test
    @DisplayName("测试包装Runnable后清理上下文")
    void testWrapRunnableClearsContext() throws InterruptedException {
        // 准备测试数据
        AtomicReference<Boolean> contextExistsAfterExecution = new AtomicReference<>(true);
        CountDownLatch latch = new CountDownLatch(1);
        
        // 创建原始Runnable
        Runnable originalRunnable = () -> {
            try {
                // 确认上下文存在
                assertNotNull(TenantContextHolder.getContext());
            } finally {
                latch.countDown();
            }
        };
        
        // 使用传播器包装Runnable
        Runnable wrappedRunnable = TenantContextPropagator.wrap(originalRunnable);
        
        // 在新线程中执行包装后的Runnable
        executorService.execute(() -> {
            wrappedRunnable.run();
            // 检查执行后上下文是否被清理
            contextExistsAfterExecution.set(TenantContextHolder.getContext() != null);
        });
        
        // 等待执行完成
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        
        // 给线程一点时间完成后续操作
        Thread.sleep(100);
        
        // 验证执行后上下文被清理
        assertFalse(contextExistsAfterExecution.get(), "执行完成后应清理租户上下文");
    }
    
    @Test
    @DisplayName("测试包装Callable后清理上下文")
    void testWrapCallableClearsContext() throws Exception {
        // 准备测试数据
        AtomicReference<Boolean> contextExistsAfterExecution = new AtomicReference<>(true);
        
        // 创建原始Callable
        Callable<String> originalCallable = () -> {
            // 确认上下文存在
            assertNotNull(TenantContextHolder.getContext());
            return "success";
        };
        
        // 使用传播器包装Callable
        Callable<String> wrappedCallable = TenantContextPropagator.wrap(originalCallable);
        
        // 在新线程中执行包装后的Callable
        Future<String> future = executorService.submit(() -> {
            String result = wrappedCallable.call();
            // 检查执行后上下文是否被清理
            contextExistsAfterExecution.set(TenantContextHolder.getContext() != null);
            return result;
        });
        
        // 等待执行完成
        future.get(1, TimeUnit.SECONDS);
        
        // 验证执行后上下文被清理
        assertFalse(contextExistsAfterExecution.get(), "执行完成后应清理租户上下文");
    }
}
