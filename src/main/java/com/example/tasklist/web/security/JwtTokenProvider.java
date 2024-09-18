package com.example.tasklist.web.security;

import com.example.tasklist.domain.exception.AccessDeniedException;
import com.example.tasklist.domain.exception.ResourceNotFoundException;
import com.example.tasklist.domain.user.Role;
import com.example.tasklist.domain.user.User;
import com.example.tasklist.service.UserService;
import com.example.tasklist.service.props.JwtProperties;
import com.example.tasklist.web.dto.auth.JwtResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final JwtProperties jwtProperties;
    private final UserDetailsService userDetailsService;
    private Key key;

    private final UserService userService;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    public String createAccessToken(Long userId, String name, String email, Set<Role> roles) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("id", userId);
        claims.put("name", name);
        claims.put("email", email);
        claims.put("roles", resolveRoles(roles));
        Instant validity = Instant.now()
                .plus(jwtProperties.getAccess(), ChronoUnit.HOURS);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(Date.from(validity))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long userId, String email) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("id", userId);
        Instant validity = Instant.now()
                .plus(jwtProperties.getRefresh(), ChronoUnit.DAYS);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(Date.from(validity))
                .signWith(key)
                .compact();
    }

    public JwtResponse refreshUserTokens(String refreshToken) {
        JwtResponse jwtResponse = new JwtResponse();
        if (!validateToken(refreshToken)) {
            throw new AccessDeniedException();
        }
        Long userId = Long.valueOf(getId(refreshToken));
        User user = userService.getById(userId);
        jwtResponse.setId(userId);
        jwtResponse.setName(user.getName());
        jwtResponse.setEmail(user.getEmail());
        jwtResponse.setAccessToken(createAccessToken(userId, user.getName(), user.getEmail(), user.getRoles()));
        jwtResponse.setRefreshToken(createRefreshToken(userId, user.getEmail()));
        return jwtResponse;
    }

    private List<String> resolveRoles(Set<Role> roles) {
        return roles.stream()
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJwt = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            boolean isValid = !claimsJwt.getBody().getExpiration().before(new Date());
            logger.debug("Token is valid: {}", isValid);
            return isValid;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Expired or invalid JWT token", e);
            return false;
        }
    }

    private String getId(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            String id = claims.get("id").toString();
            logger.debug("Extracted ID from token: {}", id);
            return id;
        } catch (Exception e) {
            logger.error("Failed to extract ID from token", e);
            throw new RuntimeException("Failed to extract ID from token", e);
        }
    }

    private String getEmail(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            String email = claims.getSubject();
            logger.debug("Extracted username from token: {}", email);
            return email;
        } catch (Exception e) {
            logger.error("Failed to extract username from token", e);
            throw new RuntimeException("Failed to extract username from token", e);
        }
    }

    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            logger.error("Failed to parse token", e);
            throw new RuntimeException("Failed to parse token", e);
        }
    }

    public Authentication getAuthentication(String token) {
        try {
            String email = getEmail(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (userDetails == null) {
                logger.warn("User not found: {}", email);
                throw new ResourceNotFoundException("User not found: " + email);
            }

            logger.debug("Authenticated user: {}", email);
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        } catch (ResourceNotFoundException e) {
            logger.error("Username not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Failed to authenticate token", e);
            throw new RuntimeException("Failed to authenticate token", e);
        }
    }
}
