package org.spring.trackingsystem.service;

import lombok.RequiredArgsConstructor;
import org.spring.trackingsystem.dto.ShipmentRequest;
import org.spring.trackingsystem.dto.ShipmentResponse;
import org.spring.trackingsystem.dto.StatusHistoryResponse;
import org.spring.trackingsystem.dto.StatusUpdateRequest;
import org.spring.trackingsystem.entity.*;
import org.spring.trackingsystem.exception.ResourceNotFoundException;
import org.spring.trackingsystem.repository.ShipmentRepository;
import org.spring.trackingsystem.repository.UserRepository;
import org.spring.trackingsystem.specification.ShipmentSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private static final Set<ShipmentStatus> FINAL_STATUSES = Set.of(ShipmentStatus.DELIVERED, ShipmentStatus.CANCELLED);

    private static final Map<ShipmentStatus, Set<ShipmentStatus>> ALLOWED_TRANSITIONS = Map.of(
            ShipmentStatus.CREATED,    Set.of(ShipmentStatus.IN_TRANSIT, ShipmentStatus.CANCELLED),
            ShipmentStatus.IN_TRANSIT, Set.of(ShipmentStatus.DELIVERED,  ShipmentStatus.CANCELLED)
    );

    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public ShipmentResponse create(ShipmentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Korisnik nije pronadjen"));

        if (shipmentRepository.existsByTrackingNumber(request.getTrackingNumber())) {
            throw new IllegalArgumentException(
                    "Posiljka sa brojem za pracenje '" + request.getTrackingNumber() + "' vec postoji");
        }

        Shipment shipment = Shipment.builder()
                .trackingNumber(request.getTrackingNumber())
                .description(request.getDescription())
                .status(ShipmentStatus.CREATED)
                .user(user)
                .deliveryAddress(request.getDeliveryAddress())
                .build();

        ShipmentStatusHistory history = ShipmentStatusHistory.builder()
                .shipment(shipment)
                .previousStatus(null)
                .newStatus(ShipmentStatus.CREATED)
                .note("Posiljka kreirana")
                .build();
        shipment.addStatusHistory(history);

        shipment = shipmentRepository.save(shipment);
        return mapToResponse(shipment);
    }

    @Transactional
    public ShipmentResponse updateStatus(Long shipmentId, StatusUpdateRequest request) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Posiljka nije pronadjena"));

        ShipmentStatus oldStatus = shipment.getStatus();
        ShipmentStatus newStatus = request.getNewStatus();

        if (oldStatus == newStatus) {
            return mapToResponse(shipment);
        }

        if (FINAL_STATUSES.contains(oldStatus)) {
            throw new IllegalStateException(
                    "Status posiljke se ne moze promeniti - " + oldStatus);
        }

        Set<ShipmentStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(oldStatus, Set.of());
        if (!allowed.contains(newStatus)) {
            throw new IllegalStateException(
                    "Nije moguce preci sa statusa " + oldStatus + " na status " + newStatus +
                            ". Dozvoljeni prelazi: " + allowed);
        }

        shipment.setStatus(newStatus);

        ShipmentStatusHistory history = ShipmentStatusHistory.builder()
                .shipment(shipment)
                .previousStatus(oldStatus)
                .newStatus(newStatus)
                .note(request.getNote())
                .build();
        shipment.addStatusHistory(history);

        shipment = shipmentRepository.save(shipment);
        return mapToResponse(shipment);
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getById(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Posiljka nije pronadjena"));
        return mapToResponse(shipment);
    }

    @Transactional(readOnly = true)
    public Page<ShipmentResponse> search(Long userId, ShipmentStatus status,
                                         LocalDate createdFrom, LocalDate createdTo,
                                         Pageable pageable) {
        Specification<Shipment> spec = Specification.where(ShipmentSpecification.hasUserId(userId))
                .and(ShipmentSpecification.hasStatus(status))
                .and(ShipmentSpecification.createdFrom(createdFrom))
                .and(ShipmentSpecification.createdTo(createdTo));

        return shipmentRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    private ShipmentResponse mapToResponse(Shipment shipment) {
        List<StatusHistoryResponse> history = shipment.getStatusHistory().stream()
                .map(h -> StatusHistoryResponse.builder()
                        .id(h.getId())
                        .previousStatus(h.getPreviousStatus())
                        .newStatus(h.getNewStatus())
                        .note(h.getNote())
                        .changedAt(h.getChangedAt())
                        .build())
                .collect(Collectors.toList());

        return ShipmentResponse.builder()
                .id(shipment.getId())
                .trackingNumber(shipment.getTrackingNumber())
                .description(shipment.getDescription())
                .currentStatus(shipment.getStatus())
                .userId(shipment.getUser().getId())
                .deliveryAddress(shipment.getDeliveryAddress())
                .createdAt(shipment.getCreatedAt())
                .updatedAt(shipment.getUpdatedAt())
                .statusHistory(history)
                .build();
    }

    @Transactional
    public void delete(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Posiljka nije pronadjena"));
        shipmentRepository.delete(shipment);
    }

    @Transactional
    public ShipmentResponse cancel(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Posiljka nije pronadjena"));

        if (FINAL_STATUSES.contains(shipment.getStatus())) {
            throw new IllegalStateException(
                    "Posiljka se ne moze otkazati jer je vec u finalnom stanju: " + shipment.getStatus());
        }

        ShipmentStatus oldStatus = shipment.getStatus();
        shipment.setStatus(ShipmentStatus.CANCELLED);

        ShipmentStatusHistory history = ShipmentStatusHistory.builder()
                .shipment(shipment)
                .previousStatus(oldStatus)
                .newStatus(ShipmentStatus.CANCELLED)
                .note("Posiljka otkazana")
                .build();
        shipment.addStatusHistory(history);

        shipment = shipmentRepository.save(shipment);
        return mapToResponse(shipment);
    }

    @Transactional(readOnly = true)
    public ShipmentResponse getByTrackingNumber(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Posiljka sa brojem za pracenje '" + trackingNumber + "' nije pronadjena"));
        return mapToResponse(shipment);
    }

    @Transactional
    public ShipmentResponse cancelByTrackingNumber(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Posiljka sa brojem za pracenje '" + trackingNumber + "' nije pronadjena"));

        return cancel(shipment.getId());
    }

    @Transactional
    public ShipmentResponse updateStatusByTrackingNumber(String trackingNumber, StatusUpdateRequest request) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Posiljka sa brojem za pracenje '" + trackingNumber + "' nije pronadjena"));

        return updateStatus(shipment.getId(), request);
    }
}
