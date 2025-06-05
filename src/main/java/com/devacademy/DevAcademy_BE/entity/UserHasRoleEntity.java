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
@Table(name = "VAITRO_NGUOIDUNG")
public class UserHasRoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne()
    @JoinColumn(name = "idNguoiDung")
    UserEntity userEntity;

    @ManyToOne()
    @JoinColumn(name = "idVaiTro")
    RoleEntity roleEntity;
}
