package com.devacademy.DevAcademy_BE.entity;

import com.devacademy.DevAcademy_BE.enums.ProgressType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = false")
@Table(name = "TIENDO_HOC")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProgressEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "trangThai")
    @Enumerated(EnumType.STRING)
    ProgressType status;

    @Column(name = "xoa")
    Boolean isDeleted;

    @ManyToOne()
    @JoinColumn(name = "idNguoiDung")
    UserEntity user;

    @ManyToOne()
    @JoinColumn(name = "idBaiHoc")
    LessonEntity lessonEntity;
}
