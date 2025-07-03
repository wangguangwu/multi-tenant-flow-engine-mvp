package com.wangguangwu.flowengine.spi.test;

import com.wangguangwu.flowengine.spi.annotation.SPI;

/**
 * 数据转换器接口
 * 作为 SPI 扩展点测试用例
 *
 * @author wangguangwu
 */
@SPI("json")  // 默认使用 JSON 转换器
public interface DataConverter {

    /**
     * 将对象序列化为字符串
     *
     * @param obj 要序列化的对象
     * @return 序列化后的字符串
     */
    String serialize(Object obj);

    /**
     * 将字符串反序列化为对象
     *
     * @param data  要反序列化的字符串
     * @param clazz 目标类型
     * @param <T>   目标类型
     * @return 反序列化后的对象
     */
    <T> T deserialize(String data, Class<T> clazz);
}
