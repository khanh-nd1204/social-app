package com.social_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "AUTH-CONTROLLER")
@Tag(name = "Auth Controller")
public class AuthController {

    @GetMapping("/public/hello")
    @Operation(
            summary = "Test",
            description = "Test API")
    public String init() {
        log.info("Hello API");
        return "Hello";
    }
}
