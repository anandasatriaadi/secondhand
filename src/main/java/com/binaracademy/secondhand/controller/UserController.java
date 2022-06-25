package com.binaracademy.secondhand.controller;

import com.binaracademy.secondhand.dto.RestDto;
import com.binaracademy.secondhand.dto.UserDto;
import com.binaracademy.secondhand.model.User;
import com.binaracademy.secondhand.repository.UserRepository;
import com.binaracademy.secondhand.service.UserService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<RestDto> getAllUsers() {
        if (!System.getenv("SPRING_PROFILES_ACTIVE").equals("production")) {
            return ResponseEntity.ok(new RestDto(200, "ok", userService.getAllUsers()));
        }
        return null;
    }

    @PostMapping("/register")
    public ResponseEntity<RestDto> saveUser(@RequestBody UserDto userDto) {
        // ======== Return Conflict on Username Found ========
        if (userRepository.findByUsername(userDto.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new RestDto(HttpStatus.CONFLICT.value(), "Username exists", ""));
        }

        // ======== Return Bad Request on Incomplete Request ========
        User result = userService.saveUser(userDto);
        if (result == null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RestDto(HttpStatus.BAD_REQUEST.value(), "Incomplete user data", ""));
        }

        // ======== Return Created on User Registration Success ========
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/register").toUriString());
        return ResponseEntity.created(uri).body(new RestDto(HttpStatus.CREATED.value(), "created", result));
    }
}
