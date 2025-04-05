package com.devacademy.DevAcademy_BE.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Where(clause = "is_deleted = false")
@Table(name = "CHUONG")
public class ChapterEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "tenChuong")
    String name;

    @Column(name = "thuTuChuong")
    Integer chapterOrder;

    @Column(name = "congKhai")
    Boolean isPublic;

    @Column(name = "xoa")
    Boolean isDeleted;

    @ManyToOne()
    @JoinColumn(name = "idKhoaHoc")
    CourseEntity courseEntity;
}
