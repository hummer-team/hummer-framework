package com.hummer.api;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class RefService1 {
    public RefService1() {
        System.out.println(".......a");
    }

    public void showMessage() {
        System.out.println("hell word.");
    }
}
