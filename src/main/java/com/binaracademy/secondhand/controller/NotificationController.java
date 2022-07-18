package com.binaracademy.secondhand.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.binaracademy.secondhand.dto.RestResponseDto;
import com.binaracademy.secondhand.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NotificationController {

    @Autowired
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<RestResponseDto> getNotification(Authentication authentication) {
        return ResponseEntity.ok(new RestResponseDto(200, "ok", notificationService.getNotification(authentication.getPrincipal().toString())));
    }
}
