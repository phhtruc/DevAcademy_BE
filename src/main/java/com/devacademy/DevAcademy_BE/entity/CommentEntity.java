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
@Where(clause = "xoa = false")
@Table(name = "BINHLUAN")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "noiDung", columnDefinition = "NTEXT")
    String content;

    @Column(name = "idBinhLuanGoc")
    Integer idOriginalComment;

    @Column(name = "xoa")
    Boolean isDeleted;

    @ManyToOne()
    @JoinColumn(name = "idNguoiDung")
    UserEntity user;

    @ManyToOne()
    @JoinColumn(name = "idBaiHoc")
    LessonEntity lessonEntity;
}
