package com.hummer.rest.controller;

import com.hummer.common.logger.LoggerLevelContext;
import com.hummer.common.resource.ResourceResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author edz
 */
@RestController
public class LoggerLevelContextController {
    @GetMapping("/logger/change-level/{packageName}/{level}")
    public ResourceResponse<String> changeLevel(@PathVariable("packageName") String packageName
            , @PathVariable("level") String level) {
        LoggerLevelContext.changeLoggerLevel(level, packageName);
        return ResourceResponse.ok();
    }

    @GetMapping("/logger/show-level")
    public ResourceResponse<List<String>> showAllLevel() {
        return ResourceResponse.ok(LoggerLevelContext.getAllLoggerLevel());
    }
}
