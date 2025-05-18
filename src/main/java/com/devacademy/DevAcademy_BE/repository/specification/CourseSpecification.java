package com.devacademy.DevAcademy_BE.repository.specification;

import com.devacademy.DevAcademy_BE.entity.CategoryEntity;
import com.devacademy.DevAcademy_BE.entity.CourseEntity;
import com.devacademy.DevAcademy_BE.entity.CourseHasCategoryEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class CourseSpecification {

    public static Specification<CourseEntity> hasTitle(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<CourseEntity> hasCourseType(String type) {
        return (root, query, cb) ->
                type == null ? null : cb.equal(root.get("isPublic"), Boolean.parseBoolean(type));
    }

    public static Specification<CourseEntity> sortPrice(String order) {
        return (root, query, cb) -> {
            if (order == null) return null;
            if (order.equalsIgnoreCase("ASC")) {
                assert query != null;
                query.orderBy(cb.asc(root.get("price")));
            } else if (order.equalsIgnoreCase("DESC")) {
                assert query != null;
                query.orderBy(cb.desc(root.get("price")));
            }
            return null;
        };
    }

    public static Specification<CourseEntity> hasCateId(String categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null) return null;

            Join<CourseEntity, CourseHasCategoryEntity> courseCateJoin = root.join("courseCategories", JoinType.INNER);
            Join<CourseHasCategoryEntity, CategoryEntity> categoryJoin = courseCateJoin.join("categoryEntity", JoinType.INNER);

            return cb.equal(categoryJoin.get("id"), Long.parseLong(categoryId));
        };
    }


}
