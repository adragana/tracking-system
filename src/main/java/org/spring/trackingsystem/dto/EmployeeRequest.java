package org.spring.trackingsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequest {

    @NotBlank(message = "Ime je obavezno")
    @Size(max = 150)
    private String name;

    @NotBlank(message = "Email je obavezan")
    @Email(message = "Email nije u ispravnom formatu")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "lozinka je obavezna")
    @Size(min = 8, max = 100, message = "Lozinka mora imati najmanje 8 karaktera")
    private String password;
}