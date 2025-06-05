package com.devacademy.DevAcademy_BE.repository.specification;

import com.devacademy.DevAcademy_BE.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<UserEntity> hasNameOrEmail(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return null;
            }
            String likeKeyword = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("fullName")), likeKeyword),
                    cb.like(cb.lower(root.get("email")), likeKeyword)
            );
        };
    }

    public static Specification<UserEntity> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.trim().isEmpty()) {
                return null;
            }
            return cb.equal(cb.upper(root.get("status")), status.toUpperCase());
        };
    }

}
