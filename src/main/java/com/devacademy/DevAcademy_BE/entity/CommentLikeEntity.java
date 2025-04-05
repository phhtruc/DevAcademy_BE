package com.devacademy.DevAcademy_BE.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = false")
@Table(name = "LUOTTHICHBINHLUAN")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentLikeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "xoa")
    Boolean isDeleted;

    @ManyToOne()
    @JoinColumn(name = "idNguoiDung")
    UserEntity user;

    @ManyToOne()
    @JoinColumn(name = "idBinhLuan")
    CommentEntity commentEntity;
}
