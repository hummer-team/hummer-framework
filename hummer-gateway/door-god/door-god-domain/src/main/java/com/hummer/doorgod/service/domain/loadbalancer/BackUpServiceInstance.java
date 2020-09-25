package com.hummer.doorgod.service.domain.loadbalancer;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;

import java.net.URI;
import java.util.Map;

public class BackUpServiceInstance  implements ServiceInstance {
    private String serviceId;

    private String host;

    private int port;

    private boolean secure;

    private Map<String, String> metadata;

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    /**
     * @return The service ID as registered.
     */
    @Override
    public String getServiceId() {
        return serviceId;
    }

    /**
     * @return The hostname of the registered service instance.
     */
    @Override
    public String getHost() {
        return host;
    }

    /**
     * @return The port of the registered service instance.
     */
    @Override
    public int getPort() {
        return port;
    }

    /**
     * @return Whether the port of the registered service instance uses HTTPS.
     */
    @Override
    public boolean isSecure() {
        return secure;
    }

    /**
     * @return The service URI address.
     */
    @Override
    public URI getUri() {
        return DefaultServiceInstance.getUri(this);
    }

    /**
     * @return The key / value pair metadata associated with the service instance.
     */
    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }
}
