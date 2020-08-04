package com.hummer.doorgod.service.domain.filter.factory;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;

@Slf4j
public class ResponseBodyGatewayFilterFactory
        extends AbstractGatewayFilterFactory<ResponseBodyGatewayFilterFactory.Config> {
    public ResponseBodyGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new ResponseBodyGatewayFilter(config);
    }

    public static class ResponseBodyGatewayFilter implements GatewayFilter, Ordered {
        private final Config config;

        public ResponseBodyGatewayFilter(Config config) {
            this.config = config;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            return config.enable
                    ? chain.filter(exchange.mutate().response(decorate(exchange)).build())
                    : chain.filter(exchange);
        }

        @SuppressWarnings("unchecked")
        ServerHttpResponse decorate(ServerWebExchange exchange) {
            return new ServerHttpResponseDecorator(exchange.getResponse()) {

                @Override
                public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                    Class inClass = String.class;
                    Class outClass = String.class;

                    String originalResponseContentType = exchange
                            .getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
                    HttpHeaders httpHeaders = new HttpHeaders();

                    httpHeaders.add(HttpHeaders.CONTENT_TYPE,
                            originalResponseContentType);

                    ClientResponse clientResponse = ClientResponse
                            .create(exchange.getResponse().getStatusCode())
                            .headers(headers -> headers.putAll(httpHeaders))
                            .body(Flux.from(body))
                            .build();

                    Mono modifiedBody = clientResponse.bodyToMono(inClass)
                            .flatMap(originalBody -> {

                                System.out.println(originalBody);
                                log.debug("route id {} uri {} response body size {}"
                                        , config.getRouteId()
                                        , exchange.getRequest().getURI()
                                        , originalBody.toString().getBytes().length);
                                return Mono.just(originalBody);
                            });

                    BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody,
                            outClass);
                    CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(
                            exchange, exchange.getResponse().getHeaders());
                    return bodyInserter.insert(outputMessage, new BodyInserterContext())
                            .then(Mono.defer(() -> {
                                Flux<DataBuffer> messageBody = outputMessage.getBody();
                                HttpHeaders headers = getDelegate().getHeaders();
                                if (!headers.containsKey(HttpHeaders.TRANSFER_ENCODING)) {
                                    messageBody = messageBody.doOnNext(data -> headers
                                            .setContentLength(data.readableByteCount()));
                                }
                                return getDelegate().writeWith(messageBody);
                            }));
                }

                @Override
                public Mono<Void> writeAndFlushWith(
                        Publisher<? extends Publisher<? extends DataBuffer>> body) {
                    return writeWith(Flux.from(body).flatMapSequential(p -> p));
                }
            };
        }

        @Override
        public int getOrder() {
            return Ordered.LOWEST_PRECEDENCE;
        }
    }

    public static class Config implements HasRouteId {
        private boolean enable;
        private String routeId;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        @Override
        public String getRouteId() {
            return routeId;
        }

        @Override
        public void setRouteId(String routeId) {
            this.routeId = routeId;
        }
    }
}
