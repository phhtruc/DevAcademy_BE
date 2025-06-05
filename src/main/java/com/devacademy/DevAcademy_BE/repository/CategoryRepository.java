package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    @Query("select c from CategoryEntity c " +
            "join CourseHasCategoryEntity cc on c.id = cc.categoryEntity.id " +
            "where cc.courseEntity.id = :id")
    Optional<CategoryEntity> findByCourseId(Long id);
}
