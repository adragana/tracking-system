package org.spring.trackingsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.spring.trackingsystem.dto.UserRequest;
import org.spring.trackingsystem.dto.UserResponse;
import org.spring.trackingsystem.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }


    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }
}
