package com.wangguangwu.flowengine.spi.exception;

import java.io.Serial;

/**
 * SPI异常类。
 *
 * @author wangguangwu
 */
public class SPIException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;
    public SPIException(String message) { super(message); }
    public SPIException(String message, Throwable cause) { super(message, cause); }
    public SPIException(Throwable cause) { super(cause); }
}
