package com.devacademy.DevAcademy_BE.repository;

import com.devacademy.DevAcademy_BE.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    @Query("""
            select t from TokenEntity t join UserEntity u on t.userEntity.id = u.id
            where u.id = :idUser and (t.expired = false or t.revoked = false)
            """)
    List<TokenEntity> findAllValidTokenByUser(UUID idUser);

    Optional<TokenEntity> findByToken(String token);
}
