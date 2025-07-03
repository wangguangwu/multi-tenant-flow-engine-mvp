package com.wangguangwu.flowengine.spi;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * SPI 加载工具类
 * 提供静态方法，简化 SPI 扩展点的使用
 *
 * @author wangguangwu
 */
public final class SPILoader {

    private SPILoader() {
        // 工具类，禁止实例化
    }

    /**
     * 获取扩展加载器
     *
     * @param type 扩展点接口类型
     * @param <T>  扩展点类型
     * @return 扩展加载器
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        return DefaultExtensionLoader.getExtensionLoader(type);
    }

    /**
     * 获取默认扩展点实现
     *
     * @param type 扩展点接口类型
     * @param <T>  扩展点类型
     * @return 默认扩展点实现
     */
    public static <T> T getDefaultExtension(Class<T> type) {
        return getExtensionLoader(type).getDefaultExtension();
    }

    /**
     * 根据扩展名获取扩展点实现
     *
     * @param type 扩展点接口类型
     * @param name 扩展名
     * @param <T>  扩展点类型
     * @return 扩展点实现
     */
    public static <T> T getExtension(Class<T> type, String name) {
        return getExtensionLoader(type).getExtension(name);
    }

    /**
     * 获取所有扩展点实现
     *
     * @param type 扩展点接口类型
     * @param <T>  扩展点类型
     * @return 所有扩展点实现
     */
    public static <T> Map<String, T> getAllExtensions(Class<T> type) {
        return getExtensionLoader(type).getAllExtensions();
    }

    /**
     * 获取所有扩展点实现，按照优先级排序
     *
     * @param type 扩展点接口类型
     * @param <T>  扩展点类型
     * @return 按优先级排序的扩展点实现列表
     */
    public static <T> List<T> getSortedExtensions(Class<T> type) {
        return getExtensionLoader(type).getSortedExtensions();
    }

    /**
     * 检查是否存在指定名称的扩展点实现
     *
     * @param type 扩展点接口类型
     * @param name 扩展名
     * @param <T>  扩展点类型
     * @return 是否存在
     */
    public static <T> boolean hasExtension(Class<T> type, String name) {
        return getExtensionLoader(type).hasExtension(name);
    }

    /**
     * 安全地获取扩展点实现，如果不存在则返回 Optional.empty()
     *
     * @param type 扩展点接口类型
     * @param name 扩展名
     * @param <T>  扩展点类型
     * @return 扩展点实现的 Optional 包装
     */
    public static <T> Optional<T> getExtensionOptional(Class<T> type, String name) {
        return getExtensionLoader(type).getExtensionOptional(name);
    }
}
