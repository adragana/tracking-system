package org.spring.trackingsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.spring.trackingsystem.dto.ImportResultResponse;
import org.spring.trackingsystem.dto.ShipmentRequest;
import org.spring.trackingsystem.dto.ShipmentResponse;
import org.spring.trackingsystem.dto.StatusUpdateRequest;
import org.spring.trackingsystem.entity.ShipmentStatus;
import org.spring.trackingsystem.service.ShipmentImportService;
import org.spring.trackingsystem.service.ShipmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;


@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;
    private final ShipmentImportService shipmentImportService;

    @PostMapping
    public ResponseEntity<ShipmentResponse> create(@Valid @RequestBody ShipmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shipmentService.create(request));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.getById(id));
    }


    @PatchMapping("/status/{id}")
    public ResponseEntity<ShipmentResponse> updateStatus(@PathVariable Long id,
                                                         @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(shipmentService.updateStatus(id, request));
    }


    @GetMapping
    public ResponseEntity<Page<ShipmentResponse>> search(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) ShipmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdTo,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(shipmentService.search(userId, status, createdFrom, createdTo, pageable));
    }


    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportResultResponse> importShipments(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(shipmentImportService.importFile(file));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        shipmentService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/cancel/{id}")
    public ResponseEntity<ShipmentResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.cancel(id));
    }


    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<ShipmentResponse> getByTrackingNumber(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(shipmentService.getByTrackingNumber(trackingNumber));
    }


    @PatchMapping("/tracking/cancel/{trackingNumber}")
    public ResponseEntity<ShipmentResponse> cancelByTrackingNumber(@PathVariable String trackingNumber) {
        return ResponseEntity.ok(shipmentService.cancelByTrackingNumber(trackingNumber));
    }

    @PatchMapping("/tracking/status/{trackingNumber}")
    public ResponseEntity<ShipmentResponse> updateStatusByTrackingNumber(
            @PathVariable String trackingNumber,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(shipmentService.updateStatusByTrackingNumber(trackingNumber, request));
    }
}
