package com.binaracademy.secondhand.controller;

import com.binaracademy.secondhand.dto.RestResponseDto;
import com.binaracademy.secondhand.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class NotificationController {

    @Autowired
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public RestResponseDto getNotification(Authentication authentication) {
        return new RestResponseDto(200, "ok", notificationService.getNotification(authentication.getPrincipal().toString()));
    }
}
