package com.wangguangwu.flowengine.spi.util;

import com.wangguangwu.flowengine.spi.loader.DefaultExtensionLoader;
import com.wangguangwu.flowengine.spi.loader.ExtensionLoader;

/**
 * SPI工具类，MVP版本。
 *
 * @author wangguangwu
 */
public final class SPILoader {
    private SPILoader() {}

    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        return DefaultExtensionLoader.getExtensionLoader(type);
    }

    public static <T> T getDefaultExtension(Class<T> type) {
        return getExtensionLoader(type).getDefaultExtension();
    }

    public static <T> T getExtension(Class<T> type, String name) {
        return getExtensionLoader(type).getExtension(name);
    }
}
