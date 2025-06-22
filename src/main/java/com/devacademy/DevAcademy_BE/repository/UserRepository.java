package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByEmail(String username);

    Boolean existsByEmail(String email);

    @Query("""
            SELECT DISTINCT u FROM UserEntity u
            JOIN CourseRegisterEntity cr ON u.id = cr.userEntity.id
            WHERE (:courseId IS NULL OR cr.courseEntity.id = :courseId)
            AND cr.status = 'CONFIRMED'
            """)
    Page<UserEntity> findEnrolledStudents(Pageable pageable, Long courseId);

    // status
    @Query("SELECT COUNT(DISTINCT u.id) FROM UserEntity u JOIN CourseRegisterEntity cr ON u.id = cr.userEntity.id WHERE cr.registerType = 'BUY'")
    long countStudentsWithEnrollments();

    @Query("SELECT COUNT(DISTINCT u.id) FROM UserEntity u JOIN CourseRegisterEntity cr ON u.id = cr.userEntity.id WHERE u.status = :status AND cr.registerType = 'BUY'")
    long countByStatusAndHasEnrollments(UserStatus status);
}
