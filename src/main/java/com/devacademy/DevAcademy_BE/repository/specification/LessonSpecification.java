package com.devacademy.DevAcademy_BE.repository.specification;

import com.devacademy.DevAcademy_BE.dto.lessonDTO.LessonSearchDTO;
import com.devacademy.DevAcademy_BE.entity.LessonEntity;
import org.springframework.data.jpa.domain.Specification;

public class LessonSpecification {

    public static Specification<LessonEntity> hasTitle(LessonSearchDTO searchDTO) {
        return (root, query, cb) -> {
            if (searchDTO.getName() == null && searchDTO.getIdChapter() == null) {
                return null;
            }
            if (searchDTO.getName() != null && searchDTO.getIdChapter() != null) {
                return cb.and(
                        cb.like(cb.lower(root.get("name")), "%" + searchDTO.getName().toLowerCase() + "%"),
                        cb.equal(root.get("chapterEntity").get("id"), searchDTO.getIdChapter())
                );
            }
            return cb.equal(root.get("chapterEntity").get("id"), searchDTO.getIdChapter());
        };
    }

}
