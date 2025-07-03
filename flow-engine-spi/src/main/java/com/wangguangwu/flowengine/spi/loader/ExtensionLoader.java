package com.wangguangwu.flowengine.spi.loader;

/**
 * MVP SPI扩展点加载器接口，只保留最基础方法。
 *
 * @author wangguangwu
 * @param <T> 扩展点类型
 */
public interface ExtensionLoader<T> {
    /**
     * 获取指定名称的扩展点实现
     * @param name 扩展名
     * @return 实例
     */
    T getExtension(String name);

    /**
     * 获取默认扩展点实现
     * @return 默认实例
     */
    T getDefaultExtension();
}
