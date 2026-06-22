package org.spring.trackingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.spring.trackingsystem.entity.ShipmentStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusHistoryResponse {
    private Long id;
    private ShipmentStatus previousStatus;
    private ShipmentStatus newStatus;
    private String note;
    private LocalDateTime changedAt;
}
