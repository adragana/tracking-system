package org.spring.trackingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ShipmentRequest {

    @NotNull(message = "Korisnik je obavezan")
    private Long userId;

    @NotBlank(message = "Broj za pracenje je obavezan")
    @Size(max = 50, message = "Broj za pracenje moze imati najvise 50 karaktera")
    private String trackingNumber;

    @NotBlank(message = "Opis je obavezan")
    @Size(max = 500, message = "Opis moze imati najvise 500 karaktera")
    private String description;

    @Size(max = 500, message = "Adresa moze imati najvise 500 karaktera")
    private String deliveryAddress;
}