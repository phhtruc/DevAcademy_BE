package com.devacademy.DevAcademy_BE.entity;

import com.devacademy.DevAcademy_BE.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "VAITRO")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "xoa = false")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tenVaiTro")
    RoleType name;

    @Column(name = "xoa")
    Boolean isDeleted;
}
