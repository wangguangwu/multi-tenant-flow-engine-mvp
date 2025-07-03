package com.wangguangwu.flowengine.spi.exception;

import java.io.Serial;

/**
 * SPI 异常类
 * 用于封装 SPI 加载和使用过程中的异常
 *
 * @author wangguangwu
 */
public class SPIException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public SPIException(String message) {
        super(message);
    }

    public SPIException(String message, Throwable cause) {
        super(message, cause);
    }

    public SPIException(Throwable cause) {
        super(cause);
    }
}
