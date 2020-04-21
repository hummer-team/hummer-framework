package com.hummer.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    private static final long START = System.currentTimeMillis();

    @GetMapping("/warmup")
    public String get() {
        return "ok";
    }

    @GetMapping("warmup-time")
    public String getUpTime() {
        return String.valueOf(START / 1000);
    }
}
