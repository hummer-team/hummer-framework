package com.hummer.eureka.client.config.test;

import com.hummer.eureka.client.config.ServiceInstanceHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ServiceInstanceHolderTest extends BaseTest {
    @Autowired
    private ServiceInstanceHolder holder;
    @Test
    public void getServiceInstance() {
        String service = holder.getServiceInstance("king-service");
        System.out.println(service);
    }
}
