package com.devacademy.DevAcademy_BE.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Where(clause = "is_deleted = false")
@Table(name = "KHOAHOC")
public class CourseEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "tenKhoaHoc")
    String name;

    @Column(name = "gia")
    BigDecimal price;

    @Column(name = "anhMoTa")
    String thumbnailUrl;

    @Column(name = "moTa", columnDefinition = "NTEXT")
    String description;

    @Column(name = "congKhai")
    Boolean isPublic;

    @Column(name = "xoa")
    Boolean isDeleted;
}
