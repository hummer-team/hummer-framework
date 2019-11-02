package com.hummer.api.web;

import com.alibaba.fastjson.support.spring.annotation.ResponseJSONP;
import com.hummer.rest.model.ResourceResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/25 15:54
 **/
@RestController
@ResponseJSONP
public class JsonpController {
    @ResponseJSONP
    @GetMapping(value = "/jsonp")
    public ResourceResponse jsonp() {
        return ResourceResponse.ok("test");
    }
}
