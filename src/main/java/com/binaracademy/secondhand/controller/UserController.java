package com.binaracademy.secondhand.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.binaracademy.secondhand.dto.RestResponseDto;
import com.binaracademy.secondhand.dto.UserRegisterDto;
import com.binaracademy.secondhand.dto.UserResponseDto;
import com.binaracademy.secondhand.dto.UserUploadDto;
import com.binaracademy.secondhand.model.User;
import com.binaracademy.secondhand.repository.UserRepository;
import com.binaracademy.secondhand.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<RestResponseDto> getAllUsers() {
        if (!System.getenv("SPRING_PROFILES_ACTIVE").equals("production")) {
            List<User> userResult = userService.getAllUsers();
            List<UserResponseDto> returnResult = modelMapper.map(userResult, new TypeToken<List<UserResponseDto>>() {}.getType());
            return ResponseEntity.ok(new RestResponseDto(200, "ok", returnResult));
        }
        return null;
    }

    // ========================================================================
    //   Get current logged in user
    // ========================================================================
    @GetMapping("/user-info")
    public ResponseEntity<RestResponseDto> getCurrentuser(Authentication authentication) {
        User userResult = userService.getUser(authentication.getPrincipal().toString());
        UserResponseDto returnResult = modelMapper.map(userResult, UserResponseDto.class);
        return ResponseEntity.ok(new RestResponseDto(200, "ok", returnResult));
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
                tokens.put("accessToken", accessToken);
                tokens.put("refreshToken", refreshToken);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), new RestResponseDto(200, "ok", tokens));
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper()
                    .writeValue(response.getOutputStream(), new RestResponseDto(HttpServletResponse.SC_UNAUTHORIZED, "Token expired", ""));
                log.error("Error processing refresh token");
            }
        }
    }

    // ========================================================================
    //   Register user
    // ========================================================================
    @PostMapping("/register")
    public ResponseEntity<RestResponseDto> saveUser(@RequestBody UserRegisterDto userDto) {
        // ======== Return Conflict on Username Found ========
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new RestResponseDto(HttpStatus.CONFLICT.value(), "Email exists", ""));
        }

        // ======== Return Bad Request on Incomplete Request ========
        User userResult = userService.saveUser(userDto);
        UserResponseDto returnResult = modelMapper.map(userResult, UserResponseDto.class);
        if (returnResult == null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RestResponseDto(HttpStatus.BAD_REQUEST.value(), "Incomplete user data", ""));
        }

        // ======== Return Created on User Registration Success ========
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/register").toUriString());
        return ResponseEntity.created(uri).body(new RestResponseDto(HttpStatus.CREATED.value(), "created", returnResult));
    }

    // ========================================================================
    //   Update user
    // ========================================================================
    @PutMapping("/user/update")
    public ResponseEntity<RestResponseDto> updateUser(Authentication authentication, @ModelAttribute UserUploadDto userDto) {
        // ======== Return Bad Request on Incomplete Request ========
        User userResult = userService.updateUser(authentication.getPrincipal().toString(), userDto);
        UserResponseDto returnResult = modelMapper.map(userResult, UserResponseDto.class);
        if (returnResult == null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new RestResponseDto(HttpStatus.BAD_REQUEST.value(), "Incomplete user data", ""));
        }

        // ======== Return Created on User Registration Success ========
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/register").toUriString());
        return ResponseEntity.created(uri).body(new RestResponseDto(HttpStatus.CREATED.value(), "created", returnResult));
    }

    // ========================================================================
    //   Check user data completeness for seller
    // ========================================================================
    @GetMapping("/user/check")
    public ResponseEntity<RestResponseDto> checkUser(Authentication authentication) {
        log.info("TESTING 1");
        if (userService.checkUser(authentication.getPrincipal().toString())) {
            return ResponseEntity.status(HttpStatus.OK).body(new RestResponseDto(HttpStatus.OK.value(), "User data complete", ""));
        } else {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new RestResponseDto(HttpStatus.NOT_FOUND.value(), "User data incomplete", ""));
        }
    }
}
