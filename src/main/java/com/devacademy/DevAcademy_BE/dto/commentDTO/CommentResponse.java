package com.devacademy.DevAcademy_BE.dto.commentDTO;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    Long id;
    String content;
    UUID userId;
    String username;
    String userAvatar;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Integer likeCount;
    Boolean isLikedByCurrentUser;
    Long idOriginalComment;
}
