package com.hummer.simple.dubbo.client;

import com.hummer.rest.model.ResourceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class DemoController {
    @Autowired
    private DubboConsumer consumer;

    @GetMapping("/dubbo")
    public ResourceResponse<String> dubbo(){
        return ResourceResponse.ok(consumer.hello());
    }

    @GetMapping("/add/{a}/{b}")
    public ResourceResponse<Integer> add(@PathVariable("a")Integer a,@PathVariable("b")Integer b){
        return ResourceResponse.ok(consumer.add(a,b));
    }
}
