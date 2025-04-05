package com.devacademy.DevAcademy_BE.entity;

import com.devacademy.DevAcademy_BE.enums.Sender;
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
@Table(name = "LICHSU_CHAT_AI")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatHistoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "nguoiGui")
    @Enumerated(EnumType.STRING)
    Sender sender;

    @Column(name = "noiDung", columnDefinition = "NTEXT")
    String content;

    @Column(name = "thoiGian")
    @CreationTimestamp
    LocalDateTime timestamp;

    @Column(name = "promptGoc", columnDefinition = "NTEXT")
    String OriginalPrompt;

    @Column(name = "xoa")
    Boolean isDeleted;

    @ManyToOne()
    @JoinColumn(name = "idNguoiDung")
    UserEntity user;

    @ManyToOne()
    @JoinColumn(name = "idBaiHoc")
    LessonEntity lessonEntity;
}
