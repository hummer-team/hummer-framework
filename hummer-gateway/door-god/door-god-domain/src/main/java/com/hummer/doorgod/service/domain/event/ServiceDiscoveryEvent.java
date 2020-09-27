package com.hummer.doorgod.service.domain.event;

import org.springframework.context.ApplicationEvent;

public class ServiceDiscoveryEvent extends ApplicationEvent {
    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public ServiceDiscoveryEvent(Object source) {
        super(source);
    }

    public ServiceDiscoveryEvent(Object source
            , String serviceId
            , long getInstanceListCostMillis
            , long chooseInstanceCostMillis) {
        super(source);
        this.serviceId = serviceId;
        this.getInstanceListCostMillis = getInstanceListCostMillis;
        this.chooseInstanceCostMillis = chooseInstanceCostMillis;
    }

    public ServiceDiscoveryEvent(Object source, String serviceId, long getInstanceListCostMillis) {
        super(source);
        this.serviceId = serviceId;
        this.getInstanceListCostMillis = getInstanceListCostMillis;
    }

    public String getServiceId() {
        return serviceId;
    }

    public long getGetInstanceListCostMillis() {
        return getInstanceListCostMillis;
    }

    public long getChooseInstanceCostMillis() {
        return chooseInstanceCostMillis;
    }

    private String serviceId;
    private long getInstanceListCostMillis;
    private long chooseInstanceCostMillis;

    @Override
    public String toString() {
        return "{" +
                "serviceId='" + serviceId + '\'' +
                ", getInstanceListCostMillis=" + getInstanceListCostMillis +
                ", chooseInstanceCostMillis=" + chooseInstanceCostMillis +
                '}';
    }
}
