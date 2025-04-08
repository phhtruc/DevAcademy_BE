package com.devacademy.DevAcademy_BE.entity;

import com.devacademy.DevAcademy_BE.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "TOKEN")
@Where(clause = "xoa = false")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TokenEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "token")
    String token;

    @Column(name = "token_type")
    @Enumerated(EnumType.STRING)
    TokenType tokenType;

    @Column(name = "daHetHan")
    boolean expired;

    @Column(name = "biThuHoi")
    boolean revoked;

    @Column(name = "xoa")
    Boolean isDeleted;

    @ManyToOne()
    @JoinColumn(name = "idNguoiDung")
    UserEntity userEntity;
}
