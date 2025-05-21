package com.devacademy.DevAcademy_BE.controller;

import com.devacademy.DevAcademy_BE.dto.commentDTO.CommentRequest;
import com.devacademy.DevAcademy_BE.dto.commentDTO.ReplyCommentRequest;
import com.devacademy.DevAcademy_BE.service.CommentService;
import com.devacademy.DevAcademy_BE.util.JsonResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {

    CommentService commentService;

    @PostMapping("/lessons/{lessonId}")
    public ResponseEntity<?> createComment(@PathVariable Long lessonId,
                                           @Valid @RequestBody CommentRequest request,
                                           Authentication authentication) {
        return JsonResponse.ok(commentService.createComment(lessonId, request, authentication));
    }

    @PostMapping("/{commentId}/replies")
    public ResponseEntity<?> replyToComment(@PathVariable Long commentId,
                                            @Valid @RequestBody CommentRequest request,
                                            Authentication authentication) {
        return JsonResponse.ok(commentService.replyToComment(commentId, request, authentication));
    }

    @PostMapping("/{commentId}/likes")
    public ResponseEntity<?> likeComment(@PathVariable Long commentId,
                                         Authentication authentication) {
        return JsonResponse.ok(commentService.likeComment(commentId, authentication));
    }

    @DeleteMapping("/{commentId}/likes")
    public ResponseEntity<?> unlikeComment(@PathVariable Long commentId,
                                           Authentication authentication) {
        return JsonResponse.ok(commentService.unlikeComment(commentId, authentication));
    }

    @GetMapping("/lessons/{lessonId}")
    public ResponseEntity<?> getCommentsByLesson(@PathVariable Long lessonId) {
        return JsonResponse.ok(commentService.getCommentsByLesson(lessonId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
                                           Authentication authentication) {
        commentService.deleteComment(commentId, authentication);
        return JsonResponse.deleted();
    }
}