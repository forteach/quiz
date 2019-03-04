package com.forteach.quiz.util;

import io.netty.buffer.UnpooledByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author: zhangyy
 * @email: zhang10092009@hotmail.com
 * @date: 19-3-1 17:21
 * @version: 1.0
 * @description:
 */
@Slf4j
@SuppressWarnings("WeakerAccess")
public class LogUtil {

    public static final List<MediaType> LEGAL_LOG_MEDIA_TYPES = Arrays.asList(
            MediaType.TEXT_XML,
            MediaType.APPLICATION_XML,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_JSON_UTF8,
            MediaType.TEXT_PLAIN,
            MediaType.TEXT_XML);

    @SuppressWarnings("unchecked")
    public static <T extends DataBuffer> T loggingRequest(Logger log, T buffer) {
        return logging(log, ">>>>>>>>>> ", buffer);
    }

    public static <T extends DataBuffer> T loggingResponse(Logger log, T buffer) {
        return logging(log, "<<<<<<<<<< ", buffer);
    }

    private static <T extends DataBuffer> T logging(Logger log, String inOrOut, T buffer) {
        try {
            InputStream dataBuffer = buffer.asInputStream();
            byte[] bytes = IOUtil.read(dataBuffer);
            NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
            if (log.isDebugEnabled()) {
                log.debug("\n" +
                        " {} Payload    : {}", inOrOut, new String(bytes));
            }
            DataBufferUtils.release(buffer);
            return (T) nettyDataBufferFactory.wrap(bytes);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
