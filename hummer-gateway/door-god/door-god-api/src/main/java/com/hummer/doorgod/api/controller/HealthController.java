package com.hummer.doorgod.api.controller;

import com.hummer.common.http.HttpAsyncClient;
import com.hummer.common.http.HttpSyncClient;
import com.hummer.common.http.RequestCustomConfig;
import io.netty.channel.epoll.Epoll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@RestController
public class HealthController {

    @Autowired
    private HttpClient httpClient;

    @GetMapping("/warmup2")
    public Mono<String> warmup() {
        return Mono.just("ok");
    }


    @GetMapping("/warmup3")
    public Flux<String> warmup3() {
        return HttpClient
                .create()
                .baseUrl("http://192.168.0.11:9099/warmup")
                .get()
                .response(((httpClientResponse, byteBufFlux) -> byteBufFlux.asString()));
    }

    @GetMapping("/warmup6")
    public Flux<String> warmup6() {
        return httpClient
                .baseUrl("http://192.168.0.11:9099/warmup")
                .get()
                .response(((httpClientResponse, byteBufFlux) -> byteBufFlux.asString()));
    }

    @GetMapping("/warmup4")
    public Mono<String> warmup4() {
        return Mono.fromCallable(() -> HttpSyncClient.sendHttpGet("http://192.168.0.11:9099/warmup"));
    }

    @GetMapping("/warmup5")
    public Mono<String> warmup5() {
        return Mono.fromCallable(() ->
                HttpAsyncClient.create()
                        .sendGet(RequestCustomConfig.builder()
                                .setUrl("http://192.168.0.11:9099/warmup")
                                .setMethod(RequestMethod.GET)
                                .build()));

    }

    @GetMapping("/epoll")
    public Mono<String> epoll() {
        return Mono.just("epoll support-> " + Epoll.isAvailable());
    }
}
