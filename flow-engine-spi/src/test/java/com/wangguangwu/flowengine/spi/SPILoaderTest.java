package com.wangguangwu.flowengine.spi;

import com.wangguangwu.flowengine.spi.example.DataConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SPI加载器测试类
 *
 * @author wangguangwu
 */
class SPILoaderTest {

    @Test
    @DisplayName("测试获取默认扩展点实现")
    void testGetDefaultExtension() {
        DataConverter converter = SPILoader.getDefaultExtension(DataConverter.class);
        assertNotNull(converter, "默认扩展点实现不应为空");
        
        String result = converter.serialize("test");
        assertEquals("JSON:test", result, "默认应该使用JSON转换器");
        
        String deserialized = converter.deserialize("JSON:hello", String.class);
        assertEquals("hello", deserialized, "反序列化结果不正确");
    }
    
    @Test
    @DisplayName("测试获取指定名称的扩展点实现")
    void testGetExtension() {
        DataConverter jsonConverter = SPILoader.getExtension(DataConverter.class, "json");
        assertNotNull(jsonConverter, "JSON转换器不应为空");
        
        DataConverter xmlConverter = SPILoader.getExtension(DataConverter.class, "xml");
        assertNotNull(xmlConverter, "XML转换器不应为空");
        
        String jsonResult = jsonConverter.serialize("test");
        assertEquals("JSON:test", jsonResult, "JSON序列化结果不正确");
        
        String xmlResult = xmlConverter.serialize("test");
        assertEquals("<xml>test</xml>", xmlResult, "XML序列化结果不正确");
    }
    
    @Test
    @DisplayName("测试获取所有扩展点实现")
    void testGetAllExtensions() {
        Map<String, DataConverter> converters = SPILoader.getAllExtensions(DataConverter.class);
        assertNotNull(converters, "扩展点实现集合不应为空");
        assertEquals(2, converters.size(), "应该有2个扩展点实现");
        assertTrue(converters.containsKey("json"), "应该包含JSON转换器");
        assertTrue(converters.containsKey("xml"), "应该包含XML转换器");
    }
    
    @Test
    @DisplayName("测试获取排序后的扩展点实现")
    void testGetSortedExtensions() {
        List<DataConverter> converters = SPILoader.getSortedExtensions(DataConverter.class);
        assertNotNull(converters, "扩展点实现列表不应为空");
        assertEquals(2, converters.size(), "应该有2个扩展点实现");
    }
    
    @Test
    @DisplayName("测试检查扩展点实现是否存在")
    void testHasExtension() {
        assertTrue(SPILoader.hasExtension(DataConverter.class, "json"), "应该存在JSON转换器");
        assertTrue(SPILoader.hasExtension(DataConverter.class, "xml"), "应该存在XML转换器");
        assertFalse(SPILoader.hasExtension(DataConverter.class, "yaml"), "不应该存在YAML转换器");
    }
    
    @Test
    @DisplayName("测试安全获取扩展点实现")
    void testGetExtensionOptional() {
        assertTrue(SPILoader.getExtensionOptional(DataConverter.class, "json").isPresent(), "JSON转换器应该存在");
        assertTrue(SPILoader.getExtensionOptional(DataConverter.class, "xml").isPresent(), "XML转换器应该存在");
        assertFalse(SPILoader.getExtensionOptional(DataConverter.class, "yaml").isPresent(), "YAML转换器不应该存在");
    }
}
