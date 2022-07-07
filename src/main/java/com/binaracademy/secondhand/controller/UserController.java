package com.binaracademy.secondhand.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.binaracademy.secondhand.dto.ResponseUserDto;
import com.binaracademy.secondhand.dto.RestDto;
import com.binaracademy.secondhand.dto.UploadUserDto;
import com.binaracademy.secondhand.model.User;
import com.binaracademy.secondhand.repository.UserRepository;
import com.binaracademy.secondhand.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    // ========================================================================
    //   Get all users while not on production
    // ========================================================================
    @GetMapping("/users")
    public ResponseEntity<RestDto> getAllUsers() {
        if (!System.getenv("SPRING_PROFILES_ACTIVE").equals("production")) {
            return ResponseEntity.ok(new RestDto(200, "ok", userService.getAllUsers()));
        }
        return null;
    }

    // ========================================================================
    //   Get New Access Token from Refresh token
    // ========================================================================
    @GetMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh Token is missing");
        } else {
            try {
                String secretCode = System.getenv("jwtsecret") == null ? "th3r34ls3cr3t1sn0wh3r3t0b3f0und" : System.getenv("jwtsecret");

                String token = authHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256(secretCode.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);

                String username = decodedJWT.getSubject();
                User user = modelMapper.map(userService.getUser(username), User.class);

                // ======== Generate Access Token ========
                String accessToken = JWT
                    .create()
                    .withSubject(user.getEmail())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                    .withIssuer(request.getRequestURL().toString())
                    .sign(algorithm);

                // ======== Generate Refresh Token ========
                String refreshToken = JWT
                    .create()
                    .withSubject(user.getEmail())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 6 * 60 * 60 * 1000))
                    .withIssuer(request.getRequestURL().toString())
                    .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("username", user.getEmail());
                tokens.put("accessToken", accessToken);
                tokens.put("refreshToken", refreshToken);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), new RestDto(200, "ok", tokens));
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper()
                    .writeValue(response.getOutputStream(), new RestDto(HttpServletResponse.SC_UNAUTHORIZED, "Token expired", ""));
                log.error("Error processing refresh token");
            }
        }
    }

    // ========================================================================
    //   Register user
    // ========================================================================
    @PostMapping("/register")
    public ResponseEntity<RestDto> saveUser(@RequestBody UploadUserDto userDto) {
        // ======== Return Conflict on Username Found ========
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new RestDto(HttpStatus.CONFLICT.value(), "Username exists", ""));
        }

        // ======== Return Bad Request on Incomplete Request ========
        ResponseUserDto result = userService.saveUser(userDto);
        if (result == null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RestDto(HttpStatus.BAD_REQUEST.value(), "Incomplete user data", ""));
        }

        // ======== Return Created on User Registration Success ========
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/register").toUriString());
        return ResponseEntity.created(uri).body(new RestDto(HttpStatus.CREATED.value(), "created", result));
    }

    // ========================================================================
    //   Check user data completeness for seller
    // ========================================================================
    @GetMapping("/user/check")
    public ResponseEntity<RestDto> checkUser(Authentication authentication) {
        if (userService.checkUser(authentication.getPrincipal().toString())) {
            return ResponseEntity.status(HttpStatus.OK).body(new RestDto(HttpStatus.OK.value(), "User data complete", ""));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new RestDto(HttpStatus.NOT_FOUND.value(), "User data incomplete", ""));
        }
    }
}
