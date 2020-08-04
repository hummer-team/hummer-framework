package com.hummer.doorgod.service.domain.exception;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hummer.common.SysConstant;
import com.hummer.doorgod.service.domain.event.GlobalExceptionEvent;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author edz
 */
@Component
@Slf4j
@Order(-1)
@RequiredArgsConstructor
public class GlobalException implements ErrorWebExceptionHandler {
    private final ObjectMapper objectMapper;

    /**
     * Handle the given exception. A completion signal through the return value
     * indicates error handling is complete while an error signal indicates the
     * exception is still not handled.
     *
     * @param exchange the current exchange
     * @param ex       the exception to handle
     * @return {@code Mono<Void>} to indicate when exception handling is complete
     */
    @SneakyThrows
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        //
        exchange.getApplicationContext()
                .publishEvent(new GlobalExceptionEvent(this
                        , MDC.get(SysConstant.REQUEST_ID)
                        , ex
                        , exchange));

        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        if (ex instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatus());
        }

        String traceId = MDC.get(SysConstant.REQUEST_ID);

        return response
                .writeWith(Mono.fromSupplier(() -> {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    ErrorResponse er = new ErrorResponse();
                    er.setMessage(String.format("%s - %s", exchange.getRequest().getURI(), ex.getMessage()));
                    er.setTraceId(traceId);
                    byte[] by = JSON.toJSONBytes(er);
                    return bufferFactory.wrap(by);
                }));
    }

    @Data
    public static class ErrorResponse {
        private String message;
        private String traceId;
    }
}
