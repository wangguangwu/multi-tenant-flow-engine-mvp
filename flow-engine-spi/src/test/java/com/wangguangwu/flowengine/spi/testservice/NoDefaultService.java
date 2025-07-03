package com.wangguangwu.flowengine.spi.testservice;

/**
 * 用于测试没有默认实现的SPI接口
 *
 * @author wangguangwu
 */
public interface NoDefaultService {
    /**
     * 测试方法
     *
     * @return 返回消息
     */
    String doSomething();
}
