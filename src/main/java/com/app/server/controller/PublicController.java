package com.app.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public")
public class PublicController {



    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        return new ResponseEntity<>("hi", HttpStatus.OK);
    }


    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }
}
