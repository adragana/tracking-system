package org.spring.trackingsystem.service;

import lombok.RequiredArgsConstructor;
import org.spring.trackingsystem.entity.Shipment;
import org.spring.trackingsystem.entity.ShipmentStatus;
import org.spring.trackingsystem.entity.ShipmentStatusHistory;
import org.spring.trackingsystem.entity.User;
import org.spring.trackingsystem.exception.ResourceNotFoundException;
import org.spring.trackingsystem.repository.ShipmentRepository;
import org.spring.trackingsystem.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ShipmentRowPersister {

    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void persistRow(String trackingNumber, String description, String userEmail,
                           String statusRaw, String deliveryAddress) {
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException("Broj za pracenje trackingNumber je obavezan");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Opis (description) je obavezan");
        }
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("Email korisnika (userEmail) je obavezan");
        }

        String cleanTrackingNumber = trackingNumber.trim();

        if (shipmentRepository.existsByTrackingNumber(cleanTrackingNumber)) {
            throw new IllegalArgumentException(
                    "Posiljka sa brojem za pracenje '" + cleanTrackingNumber + "' vec postoji");
        }

        User user = userRepository.findByEmail(userEmail.trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Korisnik sa email adresom '" + userEmail.trim() + "' nije pronadjen"));

        ShipmentStatus status = ShipmentStatus.CREATED;
        if (statusRaw != null && !statusRaw.isBlank()) {
            try {
                status = ShipmentStatus.valueOf(statusRaw.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Nepoznat status '" + statusRaw + "'");
            }
        }

        String cleanDeliveryAddress = (deliveryAddress != null && !deliveryAddress.isBlank())
                ? deliveryAddress.trim()
                : null;

        Shipment shipment = Shipment.builder()
                .trackingNumber(cleanTrackingNumber)
                .description(description.trim())
                .status(status)
                .user(user)
                .deliveryAddress(cleanDeliveryAddress)
                .build();

        ShipmentStatusHistory history = ShipmentStatusHistory.builder()
                .shipment(shipment)
                .previousStatus(null)
                .newStatus(status)
                .note("Posiljka uvezena iz fajla")
                .build();
        shipment.addStatusHistory(history);

        shipmentRepository.save(shipment);
    }
}
