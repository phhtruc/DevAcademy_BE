package com.devacademy.DevAcademy_BE.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "xoa = false")
@Table(name = "NGUOIDUNG")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "hoTen")
    String hoTen;

    @Column(name = "email")
    String email;

    @Column(name = "matKhau")
    String matKhau;

    @Column(name = "anhDaiDien")
    String anhDaiDien;

    @Column(name = "xoa")
    Boolean xoa;

//    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
//    @JoinTable(name = "tbl_user_roles",
//            joinColumns=@JoinColumn(name="user", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name="role", referencedColumnName = "id")
//    )
//    Set<Role> roles= new HashSet<>();

}
