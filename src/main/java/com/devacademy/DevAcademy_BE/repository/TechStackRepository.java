package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.TechStackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechStackRepository extends JpaRepository<TechStackEntity, Long> {
}
