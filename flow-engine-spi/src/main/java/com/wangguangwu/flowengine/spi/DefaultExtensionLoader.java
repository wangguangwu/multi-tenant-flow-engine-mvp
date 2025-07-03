package com.wangguangwu.flowengine.spi;

import com.wangguangwu.flowengine.spi.annotation.Extension;
import com.wangguangwu.flowengine.spi.annotation.SPI;
import com.wangguangwu.flowengine.spi.exception.SPIException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * SPI 扩展加载器的默认实现
 *
 * @author wangguangwu
 */
@Slf4j
public class DefaultExtensionLoader<T> implements ExtensionLoader<T> {

    /**
     * SPI 配置文件路径前缀
     */
    private static final String SPI_DIRECTORY = "META-INF/flow-engine/";

    /**
     * 扩展加载器缓存，key 为扩展点接口类型，value 为对应的扩展加载器
     */
    private static final Map<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    /**
     * 扩展点实例缓存，key 为扩展名，value 为扩展点实例
     */
    private final Map<String, T> extensionInstances = new ConcurrentHashMap<>();

    /**
     * 扩展点实现类缓存，key 为扩展名，value 为扩展点实现类
     */
    private final Map<String, Class<? extends T>> extensionClasses = new ConcurrentHashMap<>();

    /**
     * 扩展点接口类型
     */
    private final Class<T> type;

    /**
     * 默认扩展名
     */
    private String defaultExtensionName;

    /**
     * 是否为单例模式
     */
    private boolean singleton = true;

    /**
     * 私有构造方法，防止外部直接实例化
     *
     * @param type 扩展点接口类型
     */
    private DefaultExtensionLoader(Class<T> type) {
        this.type = type;
        loadExtensionClasses();
    }

    /**
     * 获取扩展加载器实例
     *
     * @param type 扩展点接口类型
     * @param <T>  扩展点类型
     * @return 扩展加载器实例
     */
    @SuppressWarnings("unchecked")
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        if (type == null) {
            throw new SPIException("扩展点接口类型不能为空");
        }
        if (!type.isInterface()) {
            throw new SPIException("扩展点类型必须是接口: " + type);
        }
        if (!type.isAnnotationPresent(SPI.class)) {
            throw new SPIException("扩展点接口必须被 @SPI 注解标记: " + type);
        }

        // 从缓存中获取扩展加载器，如果不存在则创建
        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new DefaultExtensionLoader<>(type));
            loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

    @Override
    public Class<T> getExtensionType() {
        return type;
    }

    @Override
    public T getDefaultExtension() {
        if (StringUtils.isBlank(defaultExtensionName)) {
            throw new SPIException("未找到默认扩展点实现: " + type.getName());
        }
        return getExtension(defaultExtensionName);
    }

    @Override
    public T getExtension(String name) {
        if (StringUtils.isBlank(name)) {
            throw new SPIException("扩展名不能为空");
        }

        // 单例模式下，从缓存中获取实例
        if (singleton) {
            T instance = extensionInstances.get(name);
            if (instance == null) {
                synchronized (extensionInstances) {
                    instance = extensionInstances.get(name);
                    if (instance == null) {
                        instance = createExtension(name);
                        extensionInstances.put(name, instance);
                    }
                }
            }
            return instance;
        }

        // 非单例模式，每次创建新实例
        return createExtension(name);
    }

    @Override
    public Map<String, T> getAllExtensions() {
        Map<String, T> result = new HashMap<>();
        for (String name : extensionClasses.keySet()) {
            result.put(name, getExtension(name));
        }
        return result;
    }

    @Override
    public List<T> getSortedExtensions() {
        return extensionClasses.entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> {
                    Class<? extends T> clazz = entry.getValue();
                    Extension extension = clazz.getAnnotation(Extension.class);
                    return extension != null ? extension.order() : 0;
                }))
                .map(entry -> getExtension(entry.getKey()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasExtension(String name) {
        return extensionClasses.containsKey(name);
    }

    @Override
    public Optional<T> getExtensionOptional(String name) {
        return hasExtension(name) ? Optional.of(getExtension(name)) : Optional.empty();
    }

    @Override
    public void registerExtensionClass(String name, Class<? extends T> clazz) {
        if (StringUtils.isBlank(name)) {
            throw new SPIException("扩展名不能为空");
        }
        if (clazz == null) {
            throw new SPIException("扩展点实现类不能为空");
        }
        if (!type.isAssignableFrom(clazz)) {
            throw new SPIException("扩展点实现类必须实现接口: " + type.getName());
        }

        synchronized (extensionClasses) {
            // 检查是否已存在同名扩展点
            if (extensionClasses.containsKey(name)) {
                log.warn("扩展点 {} 已存在，将被覆盖", name);
                // 如果是单例模式，需要移除已缓存的实例
                if (singleton) {
                    extensionInstances.remove(name);
                }
            }

            // 注册扩展点实现类
            extensionClasses.put(name, clazz);
            log.info("注册扩展点实现类: {}={}", name, clazz.getName());
        }
    }

    @Override
    public void registerExtension(String name, T extension) {
        if (StringUtils.isBlank(name)) {
            throw new SPIException("扩展名不能为空");
        }
        if (extension == null) {
            throw new SPIException("扩展点实例不能为空");
        }
        if (!type.isInstance(extension)) {
            throw new SPIException("扩展点实例必须实现接口: " + type.getName());
        }

        // 注册扩展点实现类
        registerExtensionClass(name, extension.getClass());

        // 如果是单例模式，缓存实例
        if (singleton) {
            synchronized (extensionInstances) {
                extensionInstances.put(name, extension);
                log.info("注册扩展点实例: {}={}", name, extension.getClass().getName());
            }
        }
    }

    /**
     * 创建扩展点实例
     *
     * @param name 扩展名
     * @return 扩展点实例
     */
    private T createExtension(String name) {
        Class<? extends T> clazz = extensionClasses.get(name);
        if (clazz == null) {
            throw new SPIException("未找到扩展点实现: " + name + " for " + type.getName());
        }
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new SPIException("创建扩展点实例失败: " + name + " for " + type.getName(), e);
        }
    }

    /**
     * 加载扩展点实现类
     */
    private void loadExtensionClasses() {
        // 获取 @SPI 注解
        SPI spiAnnotation = type.getAnnotation(SPI.class);
        if (spiAnnotation != null) {
            defaultExtensionName = spiAnnotation.value();
            singleton = spiAnnotation.singleton();
        }

        // 加载 SPI 配置文件
        loadDirectory(SPI_DIRECTORY);
    }

    /**
     * 从指定目录加载 SPI 配置文件
     *
     * @param dir 目录路径
     */
    private void loadDirectory(String dir) {
        String fileName = dir + type.getName();
        try {
            ClassLoader classLoader = getClassLoader();
            Enumeration<URL> urls = classLoader.getResources(fileName);
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    loadResource(url);
                }
            }
        } catch (IOException e) {
            log.error("加载 SPI 配置文件失败: " + fileName, e);
        }
    }

    /**
     * 加载资源文件
     *
     * @param url 资源 URL
     */
    private void loadResource(URL url) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 忽略注释和空行
                final int ci = line.indexOf('#');
                if (ci >= 0) {
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.length() > 0) {
                    try {
                        String name = null;
                        int i = line.indexOf('=');
                        if (i > 0) {
                            name = line.substring(0, i).trim();
                            line = line.substring(i + 1).trim();
                        }
                        if (line.length() > 0) {
                            loadClass(line, name);
                        }
                    } catch (Exception e) {
                        log.error("加载 SPI 实现类失败: " + line, e);
                    }
                }
            }
        } catch (IOException e) {
            log.error("读取 SPI 配置文件失败: " + url, e);
        }
    }

    /**
     * 加载类
     *
     * @param className 类名
     * @param name      扩展名
     */
    @SuppressWarnings("unchecked")
    private void loadClass(String className, String name) {
        try {
            Class<?> clazz = Class.forName(className, true, getClassLoader());
            if (!type.isAssignableFrom(clazz)) {
                throw new SPIException("扩展点实现类必须实现接口: " + type.getName() + ", 但 " + className + " 不是");
            }

            // 如果没有通过配置文件指定名称，则尝试从注解中获取
            if (name == null || name.isEmpty()) {
                Extension extension = clazz.getAnnotation(Extension.class);
                if (extension != null) {
                    name = extension.value();
                }
            }

            if (name == null || name.isEmpty()) {
                throw new SPIException("扩展点实现类必须通过配置文件或 @Extension 注解指定名称: " + className);
            }

            Class<? extends T> oldClass = extensionClasses.get(name);
            if (oldClass != null && !oldClass.equals(clazz)) {
                log.warn("扩展点名称冲突: {} 已被 {} 注册，将被 {} 覆盖", name, oldClass.getName(), className);
            }

            extensionClasses.put(name, (Class<? extends T>) clazz);
        } catch (ClassNotFoundException e) {
            log.error("扩展点实现类未找到: " + className, e);
        }
    }

    /**
     * 获取类加载器
     *
     * @return 类加载器
     */
    private ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = DefaultExtensionLoader.class.getClassLoader();
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
        }
        return classLoader;
    }
}
