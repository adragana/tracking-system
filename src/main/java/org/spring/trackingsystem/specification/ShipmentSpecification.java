package org.spring.trackingsystem.specification;

import org.spring.trackingsystem.entity.Shipment;
import org.spring.trackingsystem.entity.ShipmentStatus;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;


public final class ShipmentSpecification {

    private ShipmentSpecification() {
    }

    public static Specification<Shipment> hasUserId(Long userId) {
        return (root, query, cb) -> userId == null
                ? null
                : cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Shipment> hasStatus(ShipmentStatus status) {
        return (root, query, cb) -> status == null
                ? null
                : cb.equal(root.get("status"), status);
    }

    public static Specification<Shipment> createdFrom(LocalDate from) {
        return (root, query, cb) -> from == null
                ? null
                : cb.greaterThanOrEqualTo(root.get("createdAt"), from.atStartOfDay());
    }

    public static Specification<Shipment> createdTo(LocalDate to) {
        return (root, query, cb) -> to == null
                ? null
                : cb.lessThanOrEqualTo(root.get("createdAt"), to.atTime(23, 59, 59));
    }
}
