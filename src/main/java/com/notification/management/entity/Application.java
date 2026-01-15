package com.notification.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "application_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "application_name", length = 100, nullable = false)
    private String name;

    @Column(name = "application_code", length = 50, nullable = false, unique = true)
    private String code;

    @Column(name = "application_department", length = 50)
    private String department;

    @Column(name = "application_owner")
    private String owner;

    @Column(name = "application_email", length = 50)
    private String email;

    @Column(name = "is_delete", length = 2)
    private String isDelete; // "00" for active, "01" for deleted or as per requirement

    @Column(name = "status", length = 2)
    private String status;

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
            this.isDelete = "N"; // Default to NOT deleted
        }
        if (this.status == null) {
            this.status = "01"; // Default status
        }
    }
}
