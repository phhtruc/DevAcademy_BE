package com.devacademy.DevAcademy_BE.dto.commentDTO;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentThreadResponse {
    private CommentResponse comment;
    private List<CommentResponse> replies;
}
