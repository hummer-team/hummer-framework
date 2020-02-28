package com.hummer.api;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class TestEvent extends ApplicationEvent {
    private String message;

    public TestEvent(Object source) {
        super(source);
        this.message = String.valueOf(source);
    }
}
