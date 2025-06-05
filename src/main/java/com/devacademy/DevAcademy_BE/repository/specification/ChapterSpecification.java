package com.devacademy.DevAcademy_BE.repository.specification;

import com.devacademy.DevAcademy_BE.dto.chapterDTO.ChapterSearchDTO;
import com.devacademy.DevAcademy_BE.entity.ChapterEntity;
import org.springframework.data.jpa.domain.Specification;

public class ChapterSpecification {

    public static Specification<ChapterEntity> hasTitleAndCourse(ChapterSearchDTO searchDTO) {
        return (root, query, cb) -> {
            if (searchDTO.getName() == null && searchDTO.getIdCourse() == null) {
                return null; // No filters applied
            }
            if (searchDTO.getName() != null && searchDTO.getIdCourse() != null) {
                return cb.and(
                        cb.like(cb.lower(root.get("name")), "%" + searchDTO.getName().toLowerCase() + "%"),
                        cb.equal(root.get("courseEntity").get("id"), searchDTO.getIdCourse())
                );
            }
            return cb.equal(root.get("courseEntity").get("id"), searchDTO.getIdCourse());
        };
    }

}
