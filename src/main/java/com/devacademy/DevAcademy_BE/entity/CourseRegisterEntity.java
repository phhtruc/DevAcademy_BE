package com.devacademy.DevAcademy_BE.entity;

import com.devacademy.DevAcademy_BE.enums.RegisterStatus;
import com.devacademy.DevAcademy_BE.enums.RegisterType;
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
@Where(clause = "xoa = false")
@Table(name = "DANGKYKHOAHOC")
public class CourseRegisterEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "loaiDangKy")
    @Enumerated(EnumType.STRING)
    RegisterType registerType;

    @Column(name = "tinhTrang")
    @Enumerated(EnumType.STRING)
    RegisterStatus status;

    @Column(name = "xoa")
    Boolean isDeleted;

    @ManyToOne()
    @JoinColumn(name = "idKhoaHoc")
    CourseEntity courseEntity;

    @ManyToOne()
    @JoinColumn(name = "idNguoiDung")
    UserEntity userEntity;
}
