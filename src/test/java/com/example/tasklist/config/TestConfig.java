//package com.example.tasklist.config;
//
//import com.example.tasklist.repository.TaskRepository;
//import com.example.tasklist.repository.UserRepository;
//import com.example.tasklist.service.props.JwtProperties;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Primary;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//@TestConfiguration
//@RequiredArgsConstructor
//public class TestConfig {
//
//    private final UserRepository userRepository;
//    private final TaskRepository taskRepository;
//    private final AuthenticationManager authenticationManager;
//
//    @Bean
//    @Primary
//    public BCryptPasswordEncoder testPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public JwtProperties jwtProperties() {
//        JwtProperties jwtProperties = new JwtProperties();
//        jwtProperties.setSecret("aGVsbG9ob3dpc2l0Z29pbmdpYW1mcm9tdWtyYWluZQ==");
//        return jwtProperties;
//    }
//
//}
