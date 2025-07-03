package com.wangguangwu.flowengine.spi.loader;

import com.wangguangwu.flowengine.spi.exception.SPIException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MVP SPI扩展点加载器最简实现。
 *
 * @param <T> 扩展点类型
 * @author wangguangwu
 */
public class DefaultExtensionLoader<T> implements ExtensionLoader<T> {
    private static final String SPI_DIRECTORY = "META-INF/flow-engine/";
    private static final Map<Class<?>, DefaultExtensionLoader<?>> LOADERS = new ConcurrentHashMap<>();
    private final Map<String, T> instances = new ConcurrentHashMap<>();
    private final Class<T> type;
    private String defaultName;

    private DefaultExtensionLoader(Class<T> type) {
        this.type = type;
        loadExtensions();
    }

    @SuppressWarnings("unchecked")
    public static <T> DefaultExtensionLoader<T> getExtensionLoader(Class<T> type) {
        Objects.requireNonNull(type, "Extension type == null");
        if (!type.isInterface()) {
            throw new SPIException("Extension type must be interface: " + type);
        }
        return (DefaultExtensionLoader<T>) LOADERS.computeIfAbsent(type, DefaultExtensionLoader::new);
    }

    @Override
    public T getExtension(String name) {
        T instance = instances.get(name);
        if (instance == null) {
            throw new SPIException("No such extension: " + name);
        }
        return instance;
    }

    @Override
    public T getDefaultExtension() {
        if (defaultName == null) {
            throw new SPIException("No default extension defined for " + type.getName());
        }
        return getExtension(defaultName);
    }

    private void loadExtensions() {
        String fileName = SPI_DIRECTORY + type.getName();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            for (URL url : java.util.Collections.list(classLoader.getResources(fileName))) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) {
                            continue;
                        }
                        String name = line;
                        String className = line;
                        int eqIdx = line.indexOf('=');
                        if (eqIdx > 0) {
                            name = line.substring(0, eqIdx).trim();
                            className = line.substring(eqIdx + 1).trim();
                        }
                        
                        Class<?> clazz = Class.forName(className, true, classLoader);
                        T instance = type.cast(clazz.getDeclaredConstructor().newInstance());
                        instances.put(name, instance);
                        
                        if ("default".equals(name)) {
                            defaultName = name;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new SPIException("Failed to load SPI extensions for " + type.getName(), e);
        }
    }
}
