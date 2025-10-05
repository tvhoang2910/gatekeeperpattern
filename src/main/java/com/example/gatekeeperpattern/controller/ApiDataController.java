package com.example.gatekeeperpattern.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiDataController {

    public record UserDto(String username, String password) {}

    @GetMapping("/data")
    public ResponseEntity<Map<String, String>> getData(@RequestParam String search) {
        return ResponseEntity.ok(Map.of("status", "Success", "query", search));
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody UserDto user) {
        return ResponseEntity.ok(Map.of("status", "Success", "user_created", user));
    }
}