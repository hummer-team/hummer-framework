package com.hummer.doorgod.service.domain.filter.factory;

import com.alibaba.cloud.nacos.ribbon.NacosRule;
import com.hummer.common.exceptions.AppException;
import com.hummer.doorgod.service.domain.loadbalancer.NacosWeightedRule;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.client.loadbalancer.reactive.Request;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.PreDestroy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

@Slf4j
public class FailoverLoadBalancerGatewayFilterFactory
        extends AbstractGatewayFilterFactory<FailoverLoadBalancerGatewayFilterFactory.Config> {

    private static final ConcurrentHashMap<String, URI> uriMap = new ConcurrentHashMap<>();
    private static final String schemeKey = "@schemekey";
    private final LoadBalancerClientFactory clientFactory;
    private final NacosRule nacosRule;

    public FailoverLoadBalancerGatewayFilterFactory(LoadBalancerClientFactory clientFactory, NacosRule nacosRule) {
        super(Config.class);
        this.clientFactory = clientFactory;
        this.nacosRule = nacosRule;
    }

    @PreDestroy
    private void dispose() {
        uriMap.clear();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new CustomReactiveLoadBalancerClientFilter(config, clientFactory, nacosRule);
    }

    public static class CustomReactiveLoadBalancerClientFilter implements GatewayFilter, Ordered {
        private final Config config;
        private final LoadBalancerClientFactory clientFactory;
        private final NacosRule nacosRule;

        public CustomReactiveLoadBalancerClientFilter(Config config
                , LoadBalancerClientFactory clientFactory
                , NacosRule nacosRule) {
            this.config = config;
            this.clientFactory = clientFactory;
            this.nacosRule = nacosRule;
        }

        /**
         * Process the Web request and (optionally) delegate to the next {@code WebFilter}
         * through the given {@link GatewayFilterChain}.
         *
         * @param exchange the current server exchange
         * @param chain    provides a way to delegate to the next filter
         * @return {@code Mono<Void>} to indicate when request processing is complete
         */
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            URI url = exchange.getRequest().getURI();
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, url);
            Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
            if (route == null) {
                throw new IllegalArgumentException("no route for " + url.getHost());
            }

            String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);
            if (Strings.isEmpty(schemePrefix)) {
                schemePrefix = route.getUri().getScheme();
            }

            if (url == null) {
                return chain.filter(exchange);
            }

            if (!"lb".equals(url.getScheme()) && !"lb".equals(schemePrefix)) {
                return chain.filter(exchange);
            }
            exchange.getAttributes().put(schemeKey, route.getUri().getHost());
            // preserve the original url
            addOriginalRequestUrl(exchange, url);

            if (log.isTraceEnabled()) {
                log.trace(ReactiveLoadBalancerClientFilter.class.getSimpleName()
                        + " url before: " + url);
            }

            String finalSchemePrefix = schemePrefix;
            return tryChoose2(exchange).doOnSuccess(response -> {
                if (!response.hasServer()) {
                    URI uri = chooseBackUpHostIfNotNull(route.getId(), url);
                    exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, uri);
                } else {
                    URI uri = exchange.getRequest().getURI();

                    // if the `lb:<scheme>` mechanism was used, use `<scheme>` as the default,
                    // if the loadbalancer doesn't provide one.
                    String overrideScheme = null;
                    if (finalSchemePrefix != null) {
                        overrideScheme = url.getScheme();
                    }

                    DelegatingServiceInstance serviceInstance = new DelegatingServiceInstance(
                            response.getServer(), overrideScheme);

                    URI requestUrl = reconstructURI(serviceInstance, uri);

                    if (log.isTraceEnabled()) {
                        log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
                    }
                    uriMap.putIfAbsent(route.getId(), requestUrl);
                    exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
                }
            }).doOnError(throwable -> {
                URI uri = chooseBackUpHostIfNotNull(route.getId(), url);
                exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, uri);
            }).then(chain.filter(exchange));
        }

        private URI chooseBackUpHostIfNotNull(String routeId, URI requestUrl) {
            if (Strings.isEmpty(config.getBackUpHost())) {
                log.warn("route {} no settings back up host", config.getRouteId());
                URI cacheUri = uriMap.get(routeId);
                if (cacheUri == null) {
                    throw new NotFoundException("No loadbalancer available for " + config.getRouteId());
                }
                log.warn("{} use cache target host {}", routeId, cacheUri.getHost());
                config.setBackUpHost(cacheUri.getHost());
            }
            //
            try {
                log.warn("{} use backup host {}", config.getRouteId(), config.getBackUpHost());
                return new URI(String.format("%s%s", config.backUpHost, requestUrl.getPath()));
            } catch (URISyntaxException e) {
                log.warn("route {} backup host format error", config.getRouteId());
                throw new AppException(50000, String.format("route %s backup host format error"
                        , config.getRouteId()), e);
            }
        }

        protected URI reconstructURI(ServiceInstance serviceInstance, URI original) {
            return LoadBalancerUriTools.reconstructURI(serviceInstance, original);
        }

        private Mono<Response<ServiceInstance>> tryChoose2(ServerWebExchange exchange) {
            Mono<Response<ServiceInstance>> service =
                   new NacosWeightedRule(nacosRule, (String)exchange.getAttribute(schemeKey))
                            .choose(createRequest());

            if (service == null) {
                URI uri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
                throw new NotFoundException("No loadbalancer available for " + uri.getHost());
            }

            return service;
        }

        private Mono<Response<ServiceInstance>> tryChoose(ServerWebExchange exchange) {
            URI uri = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
            ReactorLoadBalancer<ServiceInstance> loadBalancer =
                    this.clientFactory.getInstance(uri.getHost()
                            , ReactorLoadBalancer.class
                            , ServiceInstance.class);
            if (loadBalancer != null) {
                return loadBalancer.choose(createRequest());
            }
            throw new NotFoundException("No loadbalancer available for " + uri.getHost());
        }

        private Request createRequest() {
            return ReactiveLoadBalancer.REQUEST;
        }

        @Override
        public int getOrder() {
            return config.getOrder() == null ? 10160 : config.getOrder();
        }
    }

    public static class Config implements HasRouteId {
        private String backUpHost;
        private Integer order;

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public String getBackUpHost() {
            return backUpHost;
        }

        public void setBackUpHost(String backUpHost) {
            this.backUpHost = backUpHost;
        }

        @Override
        public String getRouteId() {
            return null;
        }

        @Override
        public void setRouteId(String routeId) {

        }
    }
}
