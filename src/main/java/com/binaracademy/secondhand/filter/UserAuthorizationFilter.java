package com.binaracademy.secondhand.filter;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAuthorizationFilter extends OncePerRequestFilter{
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if(request.getServletPath().equals("/api/login")) {
            filterChain.doFilter(request, response);
        } else {
            String authHeader = request.getHeader("Authorization");
            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                filterChain.doFilter(request, response);
            } else {
                try {
                    String secretCode = System.getenv("jwtsecret") == null ? "th3r34ls3cr3t1sn0wh3r3t0b3f0und" : System.getenv("jwtsecret");
                    
                    String token = authHeader.substring("Bearer ".length());
                    Algorithm algorithm = Algorithm.HMAC256(secretCode.getBytes());
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    DecodedJWT decodedJWT = verifier.verify(token);
    
                    String username = decodedJWT.getSubject();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<SimpleGrantedAuthority>());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
    
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    log.error("Error processing JWT", e);
                }

            }
        }
    }
    
}
