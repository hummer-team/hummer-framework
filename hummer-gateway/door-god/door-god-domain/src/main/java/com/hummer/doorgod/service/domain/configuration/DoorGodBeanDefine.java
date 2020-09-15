package com.hummer.doorgod.service.domain.configuration;

import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.hummer.doorgod.service.domain.filter.GlobalLoadBalancerFilter;
import com.hummer.doorgod.service.domain.filter.factory.AddRequestHeader2GatewayFilterFactory;
import com.hummer.doorgod.service.domain.filter.factory.ProvideServiceTimeGatewayFilterFactory;
import com.hummer.doorgod.service.domain.filter.factory.RequestBlacklistGatewayFilterFactory;
import com.hummer.doorgod.service.domain.filter.factory.ResponseBodyGatewayFilterFactory;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.cloud.gateway.config.HttpClientProperties;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.ProxyProvider;

import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.springframework.cloud.gateway.config.HttpClientProperties.Pool.PoolType.DISABLED;
import static org.springframework.cloud.gateway.config.HttpClientProperties.Pool.PoolType.FIXED;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class DoorGodBeanDefine {
    @Bean
    public ProvideServiceTimeGatewayFilterFactory provideServiceTimeGatewayFilterFactory() {
        return new ProvideServiceTimeGatewayFilterFactory();
    }

    @Bean
    public AddRequestHeader2GatewayFilterFactory addRequestHeaderGatewayFilterFactory() {
        return new AddRequestHeader2GatewayFilterFactory();
    }

    @Bean
    public ResponseBodyGatewayFilterFactory responseBodyGatewayFilterFactory() {
        return new ResponseBodyGatewayFilterFactory();
    }

    @Bean
    public RequestBlacklistGatewayFilterFactory requestBlacklistAssertGatewayFilterFactory() {
        return new RequestBlacklistGatewayFilterFactory();
    }

    @Bean
    public GlobalLoadBalancerFilter globalLoadBalancerFilter(
            LoadBalancerClientFactory clientFactory) {
        return new GlobalLoadBalancerFilter(clientFactory);
    }

    @Bean
    @ConditionalOnProperty(name = "csp.sentinel.enable", matchIfMissing = false)
    @Order(-1)
    public SentinelGatewayFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    @Bean
    public List<HttpClientCustomizer> httpClientCustomizers() {
        return Collections.singletonList(httpClient -> httpClient.keepAlive(true));
    }

    @Primary
    @Bean
    public HttpClient gatewayHttpClient(HttpClientProperties properties,
                                        List<HttpClientCustomizer> customizers) {

        // configure pool resources
        HttpClientProperties.Pool pool = properties.getPool();

        ConnectionProvider connectionProvider;
        if (pool.getType() == DISABLED) {
            connectionProvider = ConnectionProvider.newConnection();
        } else if (pool.getType() == FIXED) {
            ConnectionProvider.Builder builder = ConnectionProvider
                    .builder(pool.getName()).maxConnections(pool.getMaxConnections())
                    .pendingAcquireMaxCount(-1).pendingAcquireTimeout(
                            Duration.ofMillis(pool.getAcquireTimeout()));
            if (pool.getMaxIdleTime() != null) {
                builder.maxIdleTime(pool.getMaxIdleTime());
            }
            if (pool.getMaxLifeTime() != null) {
                builder.maxLifeTime(pool.getMaxLifeTime());
            }
            connectionProvider = builder.build();
        } else {
            ConnectionProvider.Builder builder = ConnectionProvider
                    .builder(pool.getName()).maxConnections(Integer.MAX_VALUE)
                    .pendingAcquireTimeout(Duration.ofMillis(0))
                    .pendingAcquireMaxCount(-1);
            if (pool.getMaxIdleTime() != null) {
                builder.maxIdleTime(pool.getMaxIdleTime());
            }
            if (pool.getMaxLifeTime() != null) {
                builder.maxLifeTime(pool.getMaxLifeTime());
            }
            connectionProvider = builder.build();
        }

        HttpClient httpClient = HttpClient.create(connectionProvider)
                // TODO: move customizations to HttpClientCustomizers
                .httpResponseDecoder(spec -> {
                    if (properties.getMaxHeaderSize() != null) {
                        // cast to int is ok, since @Max is Integer.MAX_VALUE
                        spec.maxHeaderSize(
                                (int) properties.getMaxHeaderSize().toBytes());
                    }
                    if (properties.getMaxInitialLineLength() != null) {
                        // cast to int is ok, since @Max is Integer.MAX_VALUE
                        spec.maxInitialLineLength(
                                (int) properties.getMaxInitialLineLength().toBytes());
                    }
                    return spec;
                }).tcpConfiguration(tcpClient -> {

                    if (properties.getConnectTimeout() != null) {
                        tcpClient = tcpClient
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout())
                                .option(ChannelOption.SO_KEEPALIVE, true)
                                .option(ChannelOption.TCP_NODELAY, true)
                        ;
                    }

                    // configure proxy if proxy host is set.
                    HttpClientProperties.Proxy proxy = properties.getProxy();

                    if (StringUtils.hasText(proxy.getHost())) {

                        tcpClient = tcpClient.proxy(proxySpec -> {
                            ProxyProvider.Builder builder = proxySpec
                                    .type(ProxyProvider.Proxy.HTTP)
                                    .host(proxy.getHost());

                            PropertyMapper map = PropertyMapper.get();

                            map.from(proxy::getPort).whenNonNull().to(builder::port);
                            map.from(proxy::getUsername).whenHasText()
                                    .to(builder::username);
                            map.from(proxy::getPassword).whenHasText()
                                    .to(password -> builder.password(s -> password));
                            map.from(proxy::getNonProxyHostsPattern).whenHasText()
                                    .to(builder::nonProxyHosts);
                        });
                    }
                    return tcpClient;
                });

        HttpClientProperties.Ssl ssl = properties.getSsl();
        if ((ssl.getKeyStore() != null && ssl.getKeyStore().length() > 0)
                || ssl.getTrustedX509CertificatesForTrustManager().length > 0
                || ssl.isUseInsecureTrustManager()) {
            httpClient = httpClient.secure(sslContextSpec -> {
                // configure ssl
                SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();

                X509Certificate[] trustedX509Certificates = ssl
                        .getTrustedX509CertificatesForTrustManager();
                if (trustedX509Certificates.length > 0) {
                    sslContextBuilder = sslContextBuilder
                            .trustManager(trustedX509Certificates);
                } else if (ssl.isUseInsecureTrustManager()) {
                    sslContextBuilder = sslContextBuilder
                            .trustManager(InsecureTrustManagerFactory.INSTANCE);
                }

                try {
                    sslContextBuilder = sslContextBuilder
                            .keyManager(ssl.getKeyManagerFactory());
                } catch (Exception e) {
                    log.error("http client error", e);
                }

                sslContextSpec.sslContext(sslContextBuilder)
                        .defaultConfiguration(ssl.getDefaultConfigurationType())
                        .handshakeTimeout(ssl.getHandshakeTimeout())
                        .closeNotifyFlushTimeout(ssl.getCloseNotifyFlushTimeout())
                        .closeNotifyReadTimeout(ssl.getCloseNotifyReadTimeout());
            });
        }

        if (properties.isWiretap()) {
            httpClient = httpClient.wiretap(true);
        }

        if (!CollectionUtils.isEmpty(customizers)) {
            customizers.sort(AnnotationAwareOrderComparator.INSTANCE);
            for (HttpClientCustomizer customizer : customizers) {
                httpClient = customizer.customize(httpClient);
            }
        }

        return httpClient;
    }
}
