package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.commentDTO.CommentRequest;
import com.devacademy.DevAcademy_BE.dto.commentDTO.CommentResponse;
import com.devacademy.DevAcademy_BE.dto.commentDTO.CommentThreadResponse;
import com.devacademy.DevAcademy_BE.dto.commentDTO.LikeCommentResponse;
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

import java.util.*;
import java.util.stream.Collectors;

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

        return buildCommentResponse(commentRepository.save(comment), 0, false);
    }

    @Override
    public CommentResponse replyToComment(Long commentId, CommentRequest request, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        CommentEntity parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_EXISTED));

        Integer originalCommentId = parentComment.getIdOriginalComment() != null ?
                parentComment.getIdOriginalComment() : parentComment.getId().intValue();

        CommentEntity comment = CommentEntity.builder()
                .content(request.getContent())
                .lessonEntity(parentComment.getLessonEntity())
                .user(user)
                .idOriginalComment(originalCommentId)
                .isDeleted(false)
                .build();

        return buildCommentResponse(commentRepository.save(comment), 0, false);
    }

    @Override
    public LikeCommentResponse likeComment(Long commentId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_EXISTED));

        Optional<CommentLikeEntity> existingLike = commentLikeRepository
                .findByUserIdAndCommentEntityId(user.getId(), commentId);

        boolean isLiked = false;

        if (existingLike.isEmpty()) {
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

        if (existingLike.isPresent() && !existingLike.get().getIsDeleted()) {
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
    public List<CommentThreadResponse> getCommentsByLesson(Long lessonId) {
        List<CommentEntity> parentComments = commentRepository
                .findByLessonEntityIdAndIdOriginalCommentIsNullOrderByCreatedByDesc(lessonId);

        if (parentComments.isEmpty())
            return new ArrayList<>();

        List<Long> parentIds = parentComments.stream()
                .map(CommentEntity::getId)
                .toList();
        List<CommentEntity> replies = commentRepository
                .findByIdOriginalCommentInOrderByCreatedByAsc(
                        parentIds.stream().map(Long::intValue).collect(Collectors.toList()));

        Map<Integer, List<CommentEntity>> repliesByParentId = replies.stream()
                .collect(Collectors.groupingBy(CommentEntity::getIdOriginalComment));

        List<Long> allCommentIds = new ArrayList<>(parentIds);
        allCommentIds.addAll(replies.stream().map(CommentEntity::getId).toList());

        Map<Long, Integer> likeCounts = commentLikeRepository
                .countLikesByCommentIds(allCommentIds);

        return parentComments.stream()
                .map(parent -> {
                    CommentResponse parentResponse = buildCommentResponse(
                            parent,
                            likeCounts.getOrDefault(parent.getId(), 0),
                            false);

                    List<CommentResponse> replyResponses = repliesByParentId
                            .getOrDefault(parent.getId().intValue(), new ArrayList<>())
                            .stream()
                            .map(reply -> buildCommentResponse(
                                    reply,
                                    likeCounts.getOrDefault(reply.getId(), 0),
                                    false))
                            .collect(Collectors.toList());

                    return CommentThreadResponse.builder()
                            .comment(parentResponse)
                            .replies(replyResponses)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long commentId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_EXISTED));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new ApiException(ErrorCode.DELETE_ERROR);
        }

        comment.setIsDeleted(true);
        commentRepository.save(comment);
    }

    private CommentResponse buildCommentResponse(CommentEntity comment, Integer likeCount, Boolean isLiked) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getFullName())
                .userAvatar(comment.getUser().getAvatar())
                .createdAt(comment.getCreatedDate())
                .updatedAt(comment.getModifiedDate())
                .likeCount(likeCount)
                .isLikedByCurrentUser(isLiked)
                .build();
    }
}
