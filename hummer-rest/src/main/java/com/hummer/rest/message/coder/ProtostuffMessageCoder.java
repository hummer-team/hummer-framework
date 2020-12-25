package com.hummer.rest.message.coder;

import com.fasterxml.jackson.databind.JavaType;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.hummer.common.coder.MsgPackCoder;
import com.hummer.common.coder.ProtostuffCoder;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Map;

import static com.hummer.common.SysConstant.DEFAULT_CHARSET;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/24 16:51
 **/
public class ProtostuffMessageCoder extends AbstractHttpMessageConverter<Object>
        implements GenericHttpMessageConverter<Object> {
    public static final MediaType PROTOBUFF_JSON;
    public static final MediaType PROTOBUFF_BINARY;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProtostuffMessageCoder.class);
    private static final Map<String, String> MAP = Maps.newHashMapWithExpectedSize(2);

    static {
        MAP.put("charset", DEFAULT_CHARSET.toString());
        MAP.put("q", "0.02");
        PROTOBUFF_JSON = new MediaType("application", "x-protostuff-json", MAP);
        PROTOBUFF_BINARY = new MediaType("application", "x-protostuff-binary", MAP);
    }


    public ProtostuffMessageCoder() {
        super(PROTOBUFF_JSON,PROTOBUFF_BINARY);
    }

    /**
     * Indicates whether the given class is supported by this converter.
     *
     * @param clazz the class to test for support
     * @return {@code true} if supported; {@code false} otherwise
     */
    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    /**
     * Abstract template method that reads the actual object. Invoked from {@link #read}.
     *
     * @param clazz        the type of object to return
     * @param inputMessage the HTTP input message to read from
     * @return the converted object
     * @throws IOException                     in case of I/O errors
     * @throws HttpMessageNotReadableException in case of conversion errors
     */
    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        throw new NotImplementedException();
    }

    /**
     * Abstract template method that writes the actual body. Invoked from {@link #write}.
     *
     * @param o             the object to write to the output message
     * @param outputMessage the HTTP output message to write to
     * @throws IOException                     in case of I/O errors
     * @throws HttpMessageNotWritableException in case of conversion errors
     */
    @Override
    protected void writeInternal(Object o, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            long start = System.currentTimeMillis();
            HttpHeaders httpHeaders = outputMessage.getHeaders();
            MediaType mediaType = httpHeaders.getContentType();
            byte[] content = mediaType == null || mediaType.getSubtype().equalsIgnoreCase(PROTOBUFF_JSON.getSubtype())
                    ? ProtostuffCoder.encodeWithJson(o)
                    : ProtostuffCoder.encode(o);
            httpHeaders.setContentLength(content.length);
            outputStream.write(content);
            OutputStream out = outputMessage.getBody();
            outputStream.writeTo(out);
            LOGGER.debug("protostuff encoder resp body cost {} ms,{} bytes,mediaType {}"
                    , System.currentTimeMillis() - start
                    , content.length
                    , mediaType);
        }
    }

    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        return super.canRead(contextClass, mediaType);
    }

    /**
     * Read an object of the given type form the given input message, and returns it.
     *
     * @param type         the (potentially generic) type of object to return. This type must have
     *                     previously been passed to the {@link #canRead canRead} method of this interface,
     *                     which must have returned {@code true}.
     * @param contextClass a context class for the target type, for example a class
     *                     in which the target type appears in a method signature (can be {@code null})
     * @param inputMessage the HTTP input message to read from
     * @return the converted object
     * @throws IOException                     in case of I/O errors
     * @throws HttpMessageNotReadableException in case of conversion errors
     */
    @SneakyThrows
    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        long start = System.currentTimeMillis();
        byte[] bytes = ByteStreams.toByteArray(inputMessage.getBody());
        MediaType mediaType = inputMessage.getHeaders().getContentType();
        JavaType javaType = MsgPackCoder.getJavaType(type, contextClass);
        Object o = mediaType == null || mediaType.getSubtype().equalsIgnoreCase(PROTOBUFF_JSON.getSubtype())
                ? ProtostuffCoder.decodeWithJson(bytes,  javaType.getRawClass())
                : ProtostuffCoder.decode(bytes, javaType.getRawClass());
        LOGGER.debug("protostuff decoder req body cost {} ms,{} bytes"
                , System.currentTimeMillis() - start
                , bytes.length);
        return o;
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return super.canWrite(clazz, mediaType);
    }

    /**
     * Write an given object to the given output message.
     *
     * @param o             the object to write to the output message. The type of this object must
     *                      have previously been passed to the {@link #canWrite canWrite} method of this
     *                      interface, which must have returned {@code true}.
     * @param type          the (potentially generic) type of object to write. This type must have
     *                      previously been passed to the {@link #canWrite canWrite} method of this interface,
     *                      which must have returned {@code true}. Can be {@code null} if not specified.
     * @param contentType   the content type to use when writing. May be {@code null} to
     *                      indicate that the default content type of the converter must be used. If not
     *                      {@code null}, this media type must have previously been passed to the
     *                      {@link #canWrite canWrite} method of this interface, which must have returned
     *                      {@code true}.
     * @param outputMessage the message to write to
     * @throws IOException                     in case of I/O errors
     * @throws HttpMessageNotWritableException in case of conversion errors
     * @since 4.2
     */
    @Override
    public void write(Object o, Type type, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        super.write(o, contentType, outputMessage);
    }
}
