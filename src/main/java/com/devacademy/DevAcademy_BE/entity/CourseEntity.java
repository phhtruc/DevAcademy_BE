package com.devacademy.DevAcademy_BE.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Where(clause = "xoa = false")
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

    @Column(name = "thoiHan")
    Integer duration;

    @OneToMany(mappedBy = "courseEntity")
    List<CourseHasCategoryEntity> courseCategories;
}
