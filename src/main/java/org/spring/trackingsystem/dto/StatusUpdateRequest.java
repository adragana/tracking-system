package org.spring.trackingsystem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.spring.trackingsystem.entity.ShipmentStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusUpdateRequest {

    @NotNull(message = "Novi status je obavezan")
    private ShipmentStatus newStatus;

    @Size(max = 600, message = "Napomena moze imati najvise 600 karaktera")
    private String note;
}