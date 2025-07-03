package com.wangguangwu.flowengine.spi.test;

import com.wangguangwu.flowengine.spi.annotation.Extension;
import lombok.extern.slf4j.Slf4j;

/**
 * XML 数据转换器实现
 * 用于测试 SPI 扩展机制
 *
 * @author wangguangwu
 */
@Slf4j
@Extension(value = "xml", order = 100)
public class XmlDataConverter implements DataConverter {

    @Override
    public String serialize(Object obj) {
        if (obj == null) {
            return null;
        }
        
        try {
            // 简化实现，仅用于测试
            return "<xml>" + obj.toString() + "</xml>";
        } catch (Exception e) {
            log.error("XML 序列化失败", e);
            throw new RuntimeException("XML 序列化失败", e);
        }
    }

    @Override
    public <T> T deserialize(String data, Class<T> clazz) {
        if (data == null) {
            return null;
        }
        
        try {
            // 简化实现，仅用于测试
            log.info("反序列化 XML 数据: {}", data);
            
            if (clazz == String.class) {
                // 去掉 XML 标签
                String content = data.replaceAll("<xml>|</xml>", "");
                return clazz.cast(content);
            }
            
            throw new UnsupportedOperationException("暂不支持的类型: " + clazz.getName());
        } catch (Exception e) {
            log.error("XML 反序列化失败", e);
            throw new RuntimeException("XML 反序列化失败", e);
        }
    }
}
