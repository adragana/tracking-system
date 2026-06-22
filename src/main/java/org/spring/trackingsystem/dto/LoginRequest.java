package org.spring.trackingsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginRequest {

    @NotBlank(message = "Email je obavezan")
    @Email(message = "Email nije u ispravnom formatu")
    private String email;

    @NotBlank(message = "Lozinka je obavezna")
    private String password;
}
