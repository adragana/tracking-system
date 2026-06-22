package org.spring.trackingsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_number", nullable = false, unique = true, length = 50)
    private String trackingNumber;

    @Column(nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShipmentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "delivery_address", length = 500)
    private String deliveryAddress;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("changedAt ASC")
    @Builder.Default
    private List<ShipmentStatusHistory> statusHistory = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addStatusHistory(ShipmentStatusHistory history) {
        history.setShipment(this);
        this.statusHistory.add(history);
    }
}
