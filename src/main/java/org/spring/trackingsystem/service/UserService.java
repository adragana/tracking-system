package org.spring.trackingsystem.service;

import lombok.RequiredArgsConstructor;
import org.spring.trackingsystem.dto.UserRequest;
import org.spring.trackingsystem.dto.UserResponse;
import org.spring.trackingsystem.entity.User;
import org.spring.trackingsystem.exception.ResourceNotFoundException;
import org.spring.trackingsystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Korisnik sa email adresom '" + request.getEmail() + "' vec postoji");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();

        return mapToResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronadjen"));

        userRepository.findByEmail(request.getEmail())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Email '" + request.getEmail() + "' je zauzet");
                });

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        return mapToResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Korisnik nije pronadjen");
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronadjen"));
        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
