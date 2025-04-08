package com.devacademy.DevAcademy_BE.entity;

import com.devacademy.DevAcademy_BE.enums.TypeLesson;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
@Where(clause = "xoa = false")
@Table(name = "BAIHOC")
public class LessonEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "tenBaiHoc")
    String name;

    @Column(name = "loaiBai")
    @Enumerated(EnumType.STRING)
    TypeLesson type;

    @Column(name = "thuTuBaiHoc")
    Integer lessonOrder;

    @Column(name = "noiDung", columnDefinition = "NTEXT")
    String content;

    @Column(name = "noiDungPhu", columnDefinition = "NTEXT")
    String contentRefer;

    @Column(name = "video_url")
    String videoUrl;

    @Column(name = "xoa")
    Boolean isDeleted;

    @ManyToOne()
    @JoinColumn(name = "idChuong")
    ChapterEntity chapterEntity;
}
