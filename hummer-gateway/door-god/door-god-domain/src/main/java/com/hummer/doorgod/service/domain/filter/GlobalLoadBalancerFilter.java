package com.hummer.doorgod.service.domain.filter;

import com.hummer.doorgod.service.domain.configuration.LoadBalancerConfig;
import com.hummer.doorgod.service.domain.loadbalancer.BaseReactorServiceInstanceLoadbalancer;
import com.hummer.doorgod.service.domain.route.InMemoryRouteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.reactive.DefaultRequest;
import org.springframework.cloud.client.loadbalancer.reactive.Request;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.DelegatingServiceInstance;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class GlobalLoadBalancerFilter implements GlobalFilter, Ordered {
    /**
     * notice,this filter call must before at
     * {@link org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter}.
     */
    private static final int LOAD_BALANCER_CLIENT_FILTER_ORDER = 10149;
    private final LoadBalancerClientFactory clientFactory;
    @Autowired
    private InMemoryRouteRepository routeRepository;
    @Autowired
    private Map<String, BaseReactorServiceInstanceLoadbalancer> loadbalancerMap;

    public GlobalLoadBalancerFilter(LoadBalancerClientFactory clientFactory) {
        this.clientFactory = clientFactory;
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
        URI url = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        if (url == null) {
            return chain.filter(exchange);
        }

        String schemePrefix = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR);
        if (!"lb".equals(url.getScheme()) && !"lb".equals(schemePrefix)) {
            return chain.filter(exchange);
        }

        ServerWebExchangeUtils.addOriginalRequestUrl(exchange, url);

        if (log.isTraceEnabled()) {
            log.trace(ReactiveLoadBalancerClientFilter.class.getSimpleName() + " url before: " + url);
        }

        return choose(exchange)
                .doOnNext((response) -> {
                    if (!response.hasServer()) {
                        throw NotFoundException.create(false, "Unable to find instance for "
                                + url.getHost());
                    } else {
                        URI uri = exchange.getRequest().getURI();
                        String overrideScheme = null;
                        if (schemePrefix != null) {
                            overrideScheme = url.getScheme();
                        }

                        DelegatingServiceInstance serviceInstance =
                                new DelegatingServiceInstance(response.getServer(), overrideScheme);
                        URI requestUrl = LoadBalancerUriTools.reconstructURI(serviceInstance, uri);
                        if (log.isTraceEnabled()) {
                            log.trace("LoadBalancerClientFilter url chosen: " + requestUrl);
                        }

                        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, requestUrl);
                    }
                }).then(chain.filter(exchange));
    }

    private Mono<Response<ServiceInstance>> choose(ServerWebExchange exchange) {
        URI uri = Objects.requireNonNull(exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR));
        String routeId = ((Route) Objects.requireNonNull(
                exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR))).getId();
        LoadBalancerConfig loadBalancerConfig =
                routeRepository.getLoadBalancer(routeId, () -> LoadBalancerConfig.DEFAULT);
        BaseReactorServiceInstanceLoadbalancer loadBalancer
                = loadbalancerMap.get(loadBalancerConfig.getLoadBalancerName());
        if (loadBalancer == null) {
            loadBalancer = loadbalancerMap.get("roundLb");
            log.debug("this service {} loadBalancerName {} invalid,use default loadBalancer `roundLb`"
                    , uri.getHost(), loadBalancerConfig.getLoadBalancerName());
        }
        return loadBalancer.tryChoose(createRequest(exchange, uri.getHost()), uri.getHost(), loadBalancerConfig);
    }

    private Request createRequest(ServerWebExchange exchange, String serviceId) {
        return new DefaultRequest<>(serviceId);
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER;
    }
}
