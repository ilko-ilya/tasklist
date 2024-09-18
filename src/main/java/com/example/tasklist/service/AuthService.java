package com.example.tasklist.service;

import com.example.tasklist.web.dto.auth.JwtRequest;
import com.example.tasklist.web.dto.auth.JwtResponse;

public interface AuthService {

    JwtResponse login(JwtRequest request);

    JwtResponse refresh(String refreshToken);

}
