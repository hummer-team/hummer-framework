package com.hummer.doorgod.service.domain.exception;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.hummer.common.SysConstant;
import com.hummer.common.exceptions.AppException;
import com.hummer.doorgod.service.domain.event.CustomSentinelGatewayBlockExceptionHandler;
import com.hummer.doorgod.service.domain.event.GlobalExceptionEvent;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * @author edz
 */
@Component
@Slf4j
@Order(-1)
@RequiredArgsConstructor
public class GlobalException implements ErrorWebExceptionHandler {

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
                        , null
                        , exchange
                        , ex));

        ServerHttpResponse response = exchange.getResponse();
        setResponseCode(ex, response);

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // This exception handler only handles rejection by Sentinel.
        if (!BlockException.isBlockException(ex)) {
            String traceId = MDC.get(SysConstant.REQUEST_ID);
            URI uri = (URI) exchange.getAttributes().get(GATEWAY_REQUEST_URL_ATTR);
            String url = uri == null ? exchange.getRequest().getPath().toString() : uri.toString();

            return response
                    .writeWith(Mono.fromSupplier(() -> {
                        ErrorResponse er = new ErrorResponse();
                        er.setMessage(String.format("%s --> %s"
                                , url, ex.getMessage()));
                        er.setCode(50000);
                        if (ex instanceof AppException) {
                            er.setCode(((AppException) ex).getCode());
                        }
                        er.setTraceId(traceId);
                        byte[] by = JSON.toJSONBytes(er);
                        return response.bufferFactory().wrap(by);
                    }));
        } else {
            return new CustomSentinelGatewayBlockExceptionHandler().handle(exchange, ex);
        }
    }

    private void setResponseCode(Throwable ex, ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        if (ex instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatus());
        } else if (ex instanceof AppException) {
            response.getHeaders().add("error", Integer.toString(((AppException) ex).getCode()));
        }
    }

    @Data
    public static class ErrorResponse {
        private String message;
        private String traceId;
        private Integer code;
    }
}
