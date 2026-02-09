package com.notification.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "master_data", uniqueConstraints = { @UniqueConstraint(columnNames = { "category", "master_key" }) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class MasterData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "category", length = 50, nullable = false)
    private String category; // e.g., 'units', 'currency'

    @Column(name = "master_key", length = 50, nullable = false)
    private String masterKey; // e.g., 'CM', 'USD'

    @Column(name = "master_value", length = 255, nullable = false)
    private String masterValue; // e.g., 'Centimeter', 'US Dollar'

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "status", length = 2)
    private String status;

    @Column(name = "is_delete", length = 2)
    private String isDelete;

    // Audit Columns
    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private String updatedBy;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        if (this.isDelete == null) {
            this.isDelete = "N";
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.status == null) {
            this.status = "01";
        }
    }
}
