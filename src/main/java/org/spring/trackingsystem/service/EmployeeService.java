package org.spring.trackingsystem.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.spring.trackingsystem.dto.EmployeeRequest;
import org.spring.trackingsystem.dto.EmployeeResponse;
import org.spring.trackingsystem.entity.Employee;
import org.spring.trackingsystem.repository.EmployeeRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("employee nije pronadjen"));
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException(
                    "employee sa email adresom '" + request.getEmail() + "' vec postoji");
        }

        Employee employee = Employee.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        return mapToResponse(employeeRepository.save(employee));
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .name(employee.getName())
                .email(employee.getEmail())
                .createdAt(employee.getCreatedAt())
                .build();
    }
}
