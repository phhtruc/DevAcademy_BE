package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.UserHasRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserHasRoleRepository extends JpaRepository<UserHasRoleEntity, Long> {

    void deleteAllByUserEntity_Id(UUID userId);

}
