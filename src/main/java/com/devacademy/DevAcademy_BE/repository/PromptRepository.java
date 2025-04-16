package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.PromptEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PromptRepository extends JpaRepository<PromptEntity, Long> {
    @Query("select r from PromptEntity r where r.isActive=true")
    PromptEntity findByActive();

    @Query("select r from PromptEntity r where 1=1 and r.isActive=true and r.id=:id")
    PromptEntity findByIdActive(Long id);

    Page<PromptEntity> findAllPromptByCourseEntityId(Long id, Pageable pageable);
}
