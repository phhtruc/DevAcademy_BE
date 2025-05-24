package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.CommentLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Long> {
    Optional<CommentLikeEntity> findByUserIdAndCommentEntityId(UUID userId, Long commentId);

    int countByCommentEntityIdAndIsDeletedFalse(Long commentId);

    @Query("SELECT c.commentEntity.id as commentId, COUNT(c) as likeCount FROM CommentLikeEntity c " +
            "WHERE c.commentEntity.id IN :commentIds AND c.isDeleted = false " +
            "GROUP BY c.commentEntity.id")
    List<Object[]> countLikesByCommentIdsRaw(List<Long> commentIds);

    default Map<Long, Integer> countLikesByCommentIds(List<Long> commentIds) {
        return countLikesByCommentIdsRaw(commentIds).stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> ((Number) arr[1]).intValue()
                ));
    }

    List<CommentLikeEntity> findByUserIdAndCommentEntityIdInAndIsDeletedFalse(UUID userId, List<Long> commentIds);
}
