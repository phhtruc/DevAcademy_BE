package com.devacademy.DevAcademy_BE.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Column(name="nguoiTao",updatable = false)
    @CreatedBy
    private String createdBy;

    @Column(name="ngayTao", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Column(name="ngaySua")
    @UpdateTimestamp
    private LocalDateTime modifiedDate;

    @Column(name="nguoiSua")
    @LastModifiedBy
    private String modifiedBy;
}
