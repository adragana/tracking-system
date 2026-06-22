package org.spring.trackingsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.spring.trackingsystem.dto.EmployeeRequest;
import org.spring.trackingsystem.dto.EmployeeResponse;
import org.spring.trackingsystem.dto.LoginRequest;
import org.spring.trackingsystem.dto.LoginResponse;
import org.spring.trackingsystem.service.AuthService;
import org.spring.trackingsystem.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmployeeService employeeService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }


    @PostMapping("/register")
    public ResponseEntity<EmployeeResponse> register(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(request));
    }
}