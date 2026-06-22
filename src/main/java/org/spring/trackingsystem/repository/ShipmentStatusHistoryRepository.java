package org.spring.trackingsystem.repository;

import org.spring.trackingsystem.entity.ShipmentStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentStatusHistoryRepository extends JpaRepository<ShipmentStatusHistory, Long> {
}
