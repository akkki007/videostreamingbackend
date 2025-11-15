package com.videostreamingapp.videostreamingapp.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to Video Streaming App!");
        response.put("endpoints", Map.of(
            "register", "POST /api/auth/register",
            "login", "POST /api/auth/login",
            "listVideos", "GET /api/videos?page=0&size=10&search=query",
            "getVideo", "GET /api/videos/{id}",
            "uploadVideo", "POST /api/videos/upload (requires authentication)",
            "streamVideo", "GET /api/videos/stream/{filename}"
        ));
        return response;
    }
}

