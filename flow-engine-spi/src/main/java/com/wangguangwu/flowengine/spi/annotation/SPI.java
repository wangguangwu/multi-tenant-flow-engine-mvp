package com.wangguangwu.flowengine.spi.annotation;

import java.lang.annotation.*;

/**
 * 标记一个接口为 SPI 扩展点接口
 * 被该注解标记的接口可以通过 SPI 机制进行扩展
 *
 * @author wangguangwu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SPI {

    /**
     * 默认扩展名
     * 当没有指定扩展名时，将使用此值
     */
    String value() default "";
    
    /**
     * 是否单例模式
     * 如果为 true，则每次获取扩展点实例时返回同一个实例
     * 如果为 false，则每次获取扩展点实例时创建新的实例
     */
    boolean singleton() default true;
}
