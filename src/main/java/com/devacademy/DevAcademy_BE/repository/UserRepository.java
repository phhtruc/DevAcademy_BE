package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

}
