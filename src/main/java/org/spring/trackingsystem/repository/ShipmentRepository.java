package org.spring.trackingsystem.repository;

import org.spring.trackingsystem.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Long>,
        JpaSpecificationExecutor<Shipment> {

    boolean existsByTrackingNumber(String trackingNumber);

    Optional<Shipment> findByTrackingNumber(String trackingNumber);
}
