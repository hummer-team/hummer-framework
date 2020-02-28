package com.hummer.api;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TestEventListener {

    @EventListener
    public void handleTestEvent(TestEvent event) {
        System.out.println("handle.." + event.getMessage());
    }
}
