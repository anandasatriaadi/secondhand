package com.binaracademy.secondhand.controller;

import com.binaracademy.secondhand.dto.UserDto;
import com.binaracademy.secondhand.model.User;
import com.binaracademy.secondhand.repository.UserRepository;
import com.binaracademy.secondhand.service.UserService;
import java.net.URI;
import java.util.List;
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
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/register")
    public ResponseEntity<User> saveUser(@RequestBody UserDto userDto) {
        // ======== Return Conflict on Username Found ========
        if (userRepository.findByUsername(userDto.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // ======== Return Bad Request on Incomplete Request ========
        User res = userService.saveUser(userDto);
        if (res == null) {
            return ResponseEntity.badRequest().build();
        }

        // ======== Return Created on User Registration Success ========
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/register").toUriString());
        return ResponseEntity.created(uri).body(res);
    }
}
