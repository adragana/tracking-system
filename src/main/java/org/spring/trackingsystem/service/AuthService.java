package org.spring.trackingsystem.service;

import lombok.RequiredArgsConstructor;
import org.spring.trackingsystem.Util.JwtUtil;
import org.spring.trackingsystem.dto.LoginRequest;
import org.spring.trackingsystem.dto.LoginResponse;
import org.spring.trackingsystem.entity.Employee;
import org.spring.trackingsystem.exception.ResourceNotFoundException;
import org.spring.trackingsystem.repository.EmployeeRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmployeeRepository employeeRepository;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Employee nije pronadjen"));

        return LoginResponse.builder()
                .token(jwtUtil.generateToken(employee))
                .email(employee.getEmail())
                .name(employee.getName())
                .build();
    }
}