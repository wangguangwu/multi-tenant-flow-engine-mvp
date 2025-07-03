package com.wangguangwu.flowengine.spi.annotation;

import java.lang.annotation.*;

/**
 * 标记一个类为 SPI 扩展实现
 * 被该注解标记的类将被 SPI 加载器识别为扩展点的实现类
 *
 * @author wangguangwu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Extension {

    /**
     * 扩展点名称
     * 必须指定一个唯一的名称，用于标识该扩展实现
     */
    String value();
    
    /**
     * 扩展点排序优先级
     * 数值越小优先级越高，默认为 0
     */
    int order() default 0;
}
