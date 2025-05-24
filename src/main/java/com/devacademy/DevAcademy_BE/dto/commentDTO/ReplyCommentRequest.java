package com.devacademy.DevAcademy_BE.dto.commentDTO;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReplyCommentRequest {
    private String content;
}
