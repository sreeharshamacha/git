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
@Table(name = "notification_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "template_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "template_name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "application_code", length = 50, nullable = false)
    private String applicationCode;

    @Column(name = "application_id", nullable = false)
    private UUID applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @Column(name = "subject", length = 150)
    private String subject;

    @Lob
    @Column(name = "content")
    private byte[] content;

    @Column(name = "logo")
    private String logo;

    @Column(name = "banner")
    private String banner;

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
        if (this.status == null) {
            this.status = "01";
        }
    }
}
