package com.devacademy.DevAcademy_BE.entity;

import com.devacademy.DevAcademy_BE.enums.SubmitStatus;
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
@Table(name = "BAINOP")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmissionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    SubmitStatus status;

    @Column(name = "nhanXet", columnDefinition = "NTEXT")
    String review;

    @Column(name = "nhanXetThuCong", columnDefinition = "NTEXT")
    String manualComment;

    @Column(name = "linkBaiNop")
    String submissionLink;

    @Column(name = "fileNop", columnDefinition = "NTEXT")
    String file;

    @Column(name = "noiDungBaiLam", columnDefinition = "NTEXT")
    String contentAssignment;

    @Column(name = "xoa")
    Boolean isDeleted;

    @ManyToOne()
    @JoinColumn(name = "idNguoiDung")
    UserEntity user;

    @ManyToOne()
    @JoinColumn(name = "idBaiHoc")
    LessonEntity lessonEntity;
}
