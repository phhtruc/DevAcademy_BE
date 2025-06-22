package com.devacademy.DevAcademy_BE.repository.specification;

import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.enums.RegisterType;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;

public class StudentSpecification {

    public static Specification<UserEntity> hasEnrollment() {
        return (root, query, criteriaBuilder) -> {
            if (query != null) {
                query.distinct(true);
            }
            var join = root.join("courseRegisters", JoinType.INNER);
            return criteriaBuilder.equal(join.get("registerType"), RegisterType.BUY);
        };
    }

    public static Specification<UserEntity> enrolledInCourse(Long courseId) {
        return courseId == null ? null : (root, query, criteriaBuilder) -> {
            if (query != null) {
                query.distinct(true);
            }
            var join = root.join("courseRegisters", JoinType.INNER);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(join.get("registerType"), RegisterType.BUY),
                    criteriaBuilder.equal(join.get("courseEntity").get("id"), courseId)
            );
        };
    }

    public static Specification<UserEntity> hasNameOrEmail(String searchTerm) {
        return searchTerm == null || searchTerm.trim().isEmpty() ? null : (root, query, criteriaBuilder) -> {
            String lowercaseSearch = "%" + searchTerm.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName")), lowercaseSearch),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), lowercaseSearch)
            );
        };
    }
}