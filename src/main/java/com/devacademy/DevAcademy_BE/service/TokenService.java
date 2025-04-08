package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.entity.TokenEntity;
import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.repository.TokenRepository;
import org.springframework.stereotype.Service;

@Service
public record TokenService(TokenRepository tokenRepository) {

    public void saveToken(TokenEntity token) {
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(UserEntity user){
        var validUserToken = tokenRepository.findAllValidTokenByUser(user.getId());
        if(validUserToken.isEmpty())
            return;
        validUserToken.forEach(t -> {
            t.setRevoked(true);
            t.setExpired(true);
        });
        tokenRepository.saveAll(validUserToken);
    }
}
