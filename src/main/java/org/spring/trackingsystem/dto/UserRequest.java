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
public class UserRequest {

    @NotBlank(message = "Ime je obavezno")
    @Size(max = 150, message = "Ime moze imati najvise 150 karaktera")
    private String name;

    @NotBlank(message = "Email je obavezan")
    @Email(message = "Email nije u ispravnom formatu")
    @Size(max = 150, message = "Email moze imati najvise 150 karaktera")
    private String email;

    @Size(max = 50, message = "Broj telefona moze imati najvise 50 karaktera")
    private String phone;
}
