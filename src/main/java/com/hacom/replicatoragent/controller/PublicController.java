package com.hacom.replicatoragent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/public")
public class PublicController {

    @GetMapping(value = "/status")
    public Mono<ResponseEntity<String>> getStatus() {
        return Mono.just(ResponseEntity.ok("OK"));
    }

}