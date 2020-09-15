package com.hummer.doorgod.service.domain.configuration;

import com.alibaba.fastjson.JSON;
import org.reactivestreams.Publisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "enable.fastjson.codec", matchIfMissing = true)
public class JsonCodecCustomizerConfig {
    private static final List<MimeType> DEFAULT_MIME_TYPES = Collections.unmodifiableList(
            Arrays.asList(
                    new MimeType("application", "json"),
                    new MimeType("application", "*+json")));

    @Bean
    public CodecCustomizer jsonCodecCustomizer() {
        return configurer -> {
            configurer.defaultCodecs().jackson2JsonDecoder(new JsonDecoder());
        };
    }

    public static class JsonDecoder implements Decoder<Object> {

        /**
         * Whether the decoder supports the given target element type and the MIME
         * type of the source stream.
         *
         * @param elementType the target element type for the output stream
         * @param mimeType    the mime type associated with the stream to decode
         *                    (can be {@code null} if not specified)
         * @return {@code true} if supported, {@code false} otherwise
         */
        @Override
        public boolean canDecode(ResolvableType elementType, MimeType mimeType) {
            return true;
        }

        /**
         * Decode a {@link DataBuffer} input stream into a Flux of {@code T}.
         *
         * @param inputStream the {@code DataBuffer} input stream to decode
         * @param elementType the expected type of elements in the output stream;
         *                    this type must have been previously passed to the {@link #canDecode}
         *                    method and it must have returned {@code true}.
         * @param mimeType    the MIME type associated with the input stream (optional)
         * @param hints       additional information about how to do encode
         * @return the output stream with decoded elements
         */
        @Override
        public Flux<Object> decode(Publisher<DataBuffer> inputStream
                , ResolvableType elementType
                , MimeType mimeType
                , Map<String, Object> hints) {
            //todo
            return null;
        }

        /**
         * Decode a {@link DataBuffer} input stream into a Mono of {@code T}.
         *
         * @param inputStream the {@code DataBuffer} input stream to decode
         * @param elementType the expected type of elements in the output stream;
         *                    this type must have been previously passed to the {@link #canDecode}
         *                    method and it must have returned {@code true}.
         * @param mimeType    the MIME type associated with the input stream (optional)
         * @param hints       additional information about how to do encode
         * @return the output stream with the decoded element
         */
        @Override
        public Mono<Object> decodeToMono(Publisher<DataBuffer> inputStream
                , ResolvableType elementType
                , MimeType mimeType
                , Map<String, Object> hints) {
            //todo
            return null;
        }

        /**
         * Return the list of MIME types this decoder supports.
         */
        @Override
        public List<MimeType> getDecodableMimeTypes() {
            return Collections.singletonList(MimeTypeUtils.APPLICATION_JSON);
        }
    }

    public static class JsonEncoder implements Encoder<Object> {

        @Override
        public boolean canEncode(ResolvableType elementType, MimeType mimeType) {
            return true;
        }

        @Override
        public Flux<DataBuffer> encode(Publisher<?> inputStream
                , DataBufferFactory bufferFactory
                , ResolvableType elementType
                , MimeType mimeType
                , Map<String, Object> hints) {
            if (inputStream instanceof Mono) {
                return Mono.from(inputStream)
                        .map(value -> encodeValue(value, bufferFactory))
                        .flux();
            }
            return null;
        }

        @Override
        public List<MimeType> getEncodableMimeTypes() {
            return DEFAULT_MIME_TYPES;
        }

        /**
         *
         */
        private DataBuffer encodeValue(Object value, DataBufferFactory bufferFactory) {
            byte[] bytes = JSON.toJSONBytes(value);
            DataBuffer buffer = bufferFactory.allocateBuffer(bytes.length);
            buffer.write(bytes);
            return buffer;
        }
    }
}
