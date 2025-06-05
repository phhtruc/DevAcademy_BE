package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.commentDTO.CommentRequest;
import com.devacademy.DevAcademy_BE.dto.commentDTO.CommentResponse;
import com.devacademy.DevAcademy_BE.dto.commentDTO.CommentThreadResponse;
import com.devacademy.DevAcademy_BE.dto.commentDTO.LikeCommentResponse;
import com.devacademy.DevAcademy_BE.dto.websocket.WebSocketMessage;
import com.devacademy.DevAcademy_BE.entity.CommentEntity;
import com.devacademy.DevAcademy_BE.entity.CommentLikeEntity;
import com.devacademy.DevAcademy_BE.entity.LessonEntity;
import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.enums.SocketType;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.repository.CommentLikeRepository;
import com.devacademy.DevAcademy_BE.repository.CommentRepository;
import com.devacademy.DevAcademy_BE.repository.LessonRepository;
import com.devacademy.DevAcademy_BE.service.CommentService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    SimpMessagingTemplate messagingTemplate;

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
        CommentResponse response = buildCommentResponse(commentRepository.save(comment), 0, false);

        sendCommentNotification(SocketType.COMMENT, lessonId, response, "/topic/lesson/" + lessonId + "/comments");
        return response;
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

        CommentResponse response = buildCommentResponse(commentRepository.save(comment), 0, false);

        sendCommentNotification(SocketType.REPLY, comment.getLessonEntity().getId(), response,
                "/topic/lesson/" + comment.getLessonEntity().getId() + "/comments");
        return response;
    }

    @Override
    public LikeCommentResponse likeComment(Long commentId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_EXISTED));

        Optional<CommentLikeEntity> existingLike = commentLikeRepository
                .findByUserIdAndCommentEntityId(user.getId(), commentId);

        if (existingLike.isEmpty()) {
            CommentLikeEntity commentLike = CommentLikeEntity.builder()
                    .user(user)
                    .commentEntity(comment)
                    .isDeleted(false)
                    .build();
            commentLikeRepository.save(commentLike);
        } else if (existingLike.get().getIsDeleted()) {
            existingLike.get().setIsDeleted(false);
            commentLikeRepository.save(existingLike.get());
        }

        int likeCount = commentLikeRepository.countByCommentEntityIdAndIsDeletedFalse(commentId);

        LikeCommentResponse response = LikeCommentResponse.builder()
                .commentId(commentId)
                .likeCount(likeCount)
                .isLiked(true)
                .userId(user.getId())
                .build();

        WebSocketMessage<LikeCommentResponse> message = WebSocketMessage.<LikeCommentResponse>builder()
                .type(SocketType.LIKE)
                .lessonId(comment.getLessonEntity().getId())
                .payload(response)
                .build();

        messagingTemplate.convertAndSend("/topic/lesson/" + comment.getLessonEntity().getId() + "/comments", message);
        return response;
    }

    @Override
    public LikeCommentResponse unlikeComment(Long commentId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENT_NOT_EXISTED));

        Optional<CommentLikeEntity> existingLike = commentLikeRepository
                .findByUserIdAndCommentEntityId(user.getId(), commentId);

        if (existingLike.isPresent() && !existingLike.get().getIsDeleted()) {
            existingLike.get().setIsDeleted(true);
            commentLikeRepository.save(existingLike.get());
        }

        int likeCount = commentLikeRepository.countByCommentEntityIdAndIsDeletedFalse(commentId);

        LikeCommentResponse response = LikeCommentResponse.builder()
                .commentId(commentId)
                .likeCount(likeCount)
                .isLiked(false)
                .userId(user.getId())
                .build();

        WebSocketMessage<LikeCommentResponse> message = WebSocketMessage.<LikeCommentResponse>builder()
                .type(SocketType.UNLIKE)
                .lessonId(comment.getLessonEntity().getId())
                .payload(response)
                .build();

        messagingTemplate.convertAndSend("/topic/lesson/" + comment.getLessonEntity().getId() + "/comments", message);
        return response;
    }

    @Override
    public List<CommentThreadResponse> getCommentsByLesson(Long lessonId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UUID currentUserId = user.getId();

        List<CommentEntity> parentComments = commentRepository
                .findByLessonEntityIdAndIdOriginalCommentIsNullOrderByCreatedDateDesc(lessonId);

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

        Set<Long> userLikedComments = new HashSet<>();
        if (currentUserId != null) {
            userLikedComments = commentLikeRepository.findByUserIdAndCommentEntityIdInAndIsDeletedFalse(
                            currentUserId, allCommentIds)
                    .stream()
                    .map(like -> like.getCommentEntity().getId())
                    .collect(Collectors.toSet());
        }

        final Set<Long> finalUserLikedComments = userLikedComments;

        return parentComments.stream()
                .map(parent -> {
                    CommentResponse parentResponse = buildCommentResponse(
                            parent,
                            likeCounts.getOrDefault(parent.getId(), 0),
                            finalUserLikedComments.contains(parent.getId())); // Check if user liked this comment

                    List<CommentResponse> replyResponses = repliesByParentId
                            .getOrDefault(parent.getId().intValue(), new ArrayList<>())
                            .stream()
                            .map(reply -> buildCommentResponse(
                                    reply,
                                    likeCounts.getOrDefault(reply.getId(), 0),
                                    finalUserLikedComments.contains(reply.getId()))) // Check if user liked this reply
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

        WebSocketMessage<Long> message = WebSocketMessage.<Long>builder()
                .type(SocketType.DELETE)
                .lessonId(comment.getLessonEntity().getId())
                .payload(commentId)
                .build();

        messagingTemplate.convertAndSend("/topic/lesson/" + comment.getLessonEntity().getId() + "/comments", message);
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
                .idOriginalComment(comment.getIdOriginalComment() != null ?
                        comment.getIdOriginalComment().longValue() : null)
                .build();
    }

    private void sendCommentNotification(SocketType type, Long lessonId, CommentResponse response, String url) {
        WebSocketMessage<CommentResponse> message = WebSocketMessage.<CommentResponse>builder()
                .type(type)
                .lessonId(lessonId)
                .payload(response)
                .build();

        messagingTemplate.convertAndSend(url, message);
    }
}
