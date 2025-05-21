package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.commentDTO.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    CommentResponse createComment(Long lessonId, CommentRequest request, Authentication authentication);

    CommentResponse replyToComment(Long commentId, CommentRequest request, Authentication authentication);

    LikeCommentResponse likeComment(Long commentId, Authentication authentication);

    LikeCommentResponse unlikeComment(Long commentId, Authentication authentication);

    List<CommentThreadResponse> getCommentsByLesson(Long lessonId, int page, int size);

    void deleteComment(Long commentId, Authentication authentication);
}
