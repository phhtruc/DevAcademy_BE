package com.devacademy.DevAcademy_BE.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "DANHMUCKHOAHOC")
public class CourseHasCategoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne()
    @JoinColumn(name = "idKhoaHoc")
    CourseEntity courseEntity;

    @ManyToOne()
    @JoinColumn(name = "idDanhMuc")
    CategoryEntity categoryEntity;
}
