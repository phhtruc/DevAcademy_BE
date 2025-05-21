package com.devacademy.DevAcademy_BE.dto.commentDTO;


import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class LikeCommentResponse {
    private Long commentId;
    private Integer likeCount;
    private Boolean isLiked;
}
