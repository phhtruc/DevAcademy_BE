package com.devacademy.DevAcademy_BE.entity;

import com.devacademy.DevAcademy_BE.enums.PayStatus;
import com.devacademy.DevAcademy_BE.enums.PayType;
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
@Table(name = "THANHTOAN")
public class CoursePayEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "soTien")
    BigDecimal price;

    @Column(name = "phuongThuc")
    @Enumerated(EnumType.STRING)
    PayType method;

    @Column(name = "trangThai")
    @Enumerated(EnumType.STRING)
    PayStatus status;

    @Column(name = "xoa")
    Boolean isDeleted;

    @ManyToOne()
    @JoinColumn(name = "idKhoaHoc")
    CourseEntity courseEntity;

    @ManyToOne()
    @JoinColumn(name = "idNguoiDung")
    UserEntity userEntity;
}
