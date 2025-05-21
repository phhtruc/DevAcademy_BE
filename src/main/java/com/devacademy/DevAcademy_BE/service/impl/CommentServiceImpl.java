package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.commentDTO.*;
import com.devacademy.DevAcademy_BE.entity.CommentEntity;
import com.devacademy.DevAcademy_BE.entity.LessonEntity;
import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.repository.CommentLikeRepository;
import com.devacademy.DevAcademy_BE.repository.CommentRepository;
import com.devacademy.DevAcademy_BE.repository.LessonRepository;
import com.devacademy.DevAcademy_BE.service.CommentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {

    CommentRepository commentRepository;
    CommentLikeRepository commentLikeRepository;
    LessonRepository lessonRepository;

    @Override
    public CommentResponse createComment(Long lessonId, CreateCommentRequest request, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        LessonEntity lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ApiException(ErrorCode.LESSON_NOT_EXISTED));

        CommentEntity comment = CommentEntity.builder()
                .content(request.getContent())
                .lessonEntity(lesson)
                .user(user)
                .idOriginalComment(null)
                .isDeleted(false)
                .build();

        return buildCommentResponse(commentRepository.save(comment), user.getId(), 0, false);
    }

    @Override
    public CommentResponse replyToComment(Long commentId, ReplyCommentRequest request, Authentication authentication) {
        return null;
    }

    @Override
    public LikeCommentResponse likeComment(Long commentId, Authentication authentication) {
        return null;
    }

    @Override
    public LikeCommentResponse unlikeComment(Long commentId, Authentication authentication) {
        return null;
    }

    @Override
    public List<CommentThreadResponse> getCommentsByLesson(Long lessonId, int page, int size) {
        return List.of();
    }

    @Override
    public void deleteComment(Long commentId, Authentication authentication) {

    }

    private CommentResponse buildCommentResponse(CommentEntity comment, UUID currentUserId,
                                                 Integer likeCount, Boolean isLiked) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getFullName())
                .userAvatar(comment.getUser().getAvatar())
                .createdAt(comment.getNgayTao())
                .updatedAt(comment.getNgaySua())
                .likeCount(likeCount)
                .isLikedByCurrentUser(isLiked)
                .build();
    }
}
