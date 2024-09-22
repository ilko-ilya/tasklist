package com.example.tasklist.service.impl;

import com.example.tasklist.domain.exception.ResourceNotFoundException;
import com.example.tasklist.domain.user.Role;
import com.example.tasklist.domain.user.User;
import com.example.tasklist.service.UserService;
import com.example.tasklist.web.dto.auth.JwtRequest;
import com.example.tasklist.web.dto.auth.JwtResponse;
import com.example.tasklist.web.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserService userService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_WithCorrectData_OK() {

        Long userId = 1L;
        String name = "Mila";
        String email = "email";
        String password = "password";
        Set<Role> roles = Collections.emptySet();
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        JwtRequest request = new JwtRequest();
        request.setEmail(email);
        request.setPassword(password);

        User user = new User();
        user.setId(userId);
        user.setName(name);
        user.setEmail(email);
        user.setRoles(roles);

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(jwtTokenProvider.createAccessToken(userId, name, email, roles)).thenReturn(accessToken);
        when(jwtTokenProvider.createRefreshToken(userId, email)).thenReturn(refreshToken);

        JwtResponse response = authService.login(request);
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        assertEquals(email, response.getEmail());
        assertEquals(userId, response.getId());
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    void login_WithIncorrectEmail() {
        String email = "email";
        String password = "password";
        JwtRequest request = new JwtRequest();
        request.setEmail(email);
        request.setPassword(password);

        when(userService.getUserByEmail(email)).thenThrow(ResourceNotFoundException.class);
        verifyNoInteractions(jwtTokenProvider);
        assertThrows(ResourceNotFoundException.class, () -> authService.login(request));
    }

    @Test
    void refresh() {
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        String newRefreshToken = "newRefreshToken";

        JwtResponse response = new JwtResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(newRefreshToken);
        when(jwtTokenProvider.refreshUserTokens(refreshToken)).thenReturn(response);
        JwtResponse testResponse = authService.refresh(refreshToken);
        verify(jwtTokenProvider).refreshUserTokens(refreshToken);
        assertEquals(response, testResponse);
    }
}