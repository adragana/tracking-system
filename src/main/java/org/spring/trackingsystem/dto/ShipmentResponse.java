package org.spring.trackingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.spring.trackingsystem.entity.ShipmentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentResponse {
    private Long id;
    private String trackingNumber;
    private String description;
    private ShipmentStatus currentStatus;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<StatusHistoryResponse> statusHistory;
    private String deliveryAddress;
}
