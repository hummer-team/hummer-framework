package com.hummer.rest.controller;

import org.apache.commons.lang3.time.DateFormatUtils;
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
        String startTime = DateFormatUtils.format(START, "yyyy-MM-dd'T'HH:mm:ss");
        String upTime = String.valueOf((System.currentTimeMillis() - START) / 1000);
        return String.format("startAt:%s,upTime:%s", startTime, upTime);
    }
}
