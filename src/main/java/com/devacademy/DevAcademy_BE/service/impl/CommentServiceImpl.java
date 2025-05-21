package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.commentDTO.*;
import com.devacademy.DevAcademy_BE.entity.CommentEntity;
import com.devacademy.DevAcademy_BE.entity.CommentLikeEntity;
import com.devacademy.DevAcademy_BE.entity.LessonEntity;
import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.repository.CommentLikeRepository;
import com.devacademy.DevAcademy_BE.repository.CommentRepository;
import com.devacademy.DevAcademy_BE.repository.LessonRepository;
import com.devacademy.DevAcademy_BE.service.CommentService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {

    CommentRepository commentRepository;
    CommentLikeRepository commentLikeRepository;
    LessonRepository lessonRepository;

    @Override
    public CommentResponse createComment(Long lessonId, CommentRequest request, Authentication authentication) {
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
    public CommentResponse replyToComment(Long commentId, CommentRequest request, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        CommentEntity originalComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_EXISTED));

        CommentEntity comment = CommentEntity.builder()
                .content(request.getContent())
                .lessonEntity(originalComment.getLessonEntity())
                .user(user)
                .idOriginalComment(originalComment.getIdOriginalComment())
                .isDeleted(false)
                .build();

        return buildCommentResponse(commentRepository.save(comment), user.getId(), 0, false);
    }

    @Override
    public LikeCommentResponse likeComment(Long commentId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_EXISTED));

        Optional<CommentLikeEntity> existingLike = commentLikeRepository
                .findByUserIdAndCommentEntityId(user.getId(), commentId);

        boolean isLiked = false;

        if(existingLike.isEmpty()){
            CommentLikeEntity commentLike = CommentLikeEntity.builder()
                    .user(user)
                    .commentEntity(comment)
                    .isDeleted(false)
                    .build();
            commentLikeRepository.save(commentLike);
            isLiked = true;
        } else if (existingLike.get().getIsDeleted()) {
            existingLike.get().setIsDeleted(false);
            commentLikeRepository.save(existingLike.get());
        } else {
            isLiked = true;
        }

        int likeCount = commentLikeRepository.countByCommentEntityIdAndIsDeletedFalse(commentId);
        return LikeCommentResponse.builder()
                .commentId(commentId)
                .likeCount(likeCount)
                .isLiked(isLiked)
                .build();
    }

    @Override
    public LikeCommentResponse unlikeComment(Long commentId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_EXISTED));

        Optional<CommentLikeEntity> existingLike = commentLikeRepository
                .findByUserIdAndCommentEntityId(user.getId(), commentId);

        if(existingLike.isPresent() && !existingLike.get().getIsDeleted()){
            existingLike.get().setIsDeleted(true);
            commentLikeRepository.save(existingLike.get());
        }

        int likeCount = commentLikeRepository.countByCommentEntityIdAndIsDeletedFalse(commentId);
        return LikeCommentResponse.builder()
                .commentId(commentId)
                .likeCount(likeCount)
                .isLiked(false)
                .build();
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
