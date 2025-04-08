package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.userDTO.UserRequestDTO;
import com.devacademy.DevAcademy_BE.dto.userDTO.UserResponseDTO;
import com.devacademy.DevAcademy_BE.entity.RoleEntity;
import com.devacademy.DevAcademy_BE.entity.TokenEntity;
import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.entity.UserHasRoleEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.enums.RoleType;
import com.devacademy.DevAcademy_BE.enums.TokenType;
import com.devacademy.DevAcademy_BE.enums.UserStatus;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.mapper.UserMapper;
import com.devacademy.DevAcademy_BE.repository.RoleRepository;
import com.devacademy.DevAcademy_BE.repository.TokenRepository;
import com.devacademy.DevAcademy_BE.repository.UserHasRoleRepository;
import com.devacademy.DevAcademy_BE.repository.UserRepository;
import com.devacademy.DevAcademy_BE.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;
    UserHasRoleRepository userHasRoleRepository;
    PasswordEncoder passwordEncoder;
    TokenRepository tokenRepository;

    @Override
    public UserResponseDTO getUserById(UUID id) {
        return null;
    }

    @Override
    public UserResponseDTO addUser(UserRequestDTO request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new ApiException(ErrorCode.EMAIL_EXISTS);
        }

        UserEntity user = userMapper.toUserEntity(request);
        user.setIsDeleted(false);
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var savedUser = userRepository.save(user);

        RoleEntity userRole = roleRepository.findByName(RoleType.USER)
                .orElseThrow(() -> new ApiException(ErrorCode.ROLE_NAME_NOT_FOUND));

        UserHasRoleEntity userHasRole = new UserHasRoleEntity();
        userHasRole.setUserEntity(savedUser);
        userHasRole.setRoleEntity(userRole);
        userHasRoleRepository.save(userHasRole);

        return userMapper.toUserResponseDTO(savedUser);
    }

    @Override
    public PageResponse<?> getAllUser(int page, int pageSize) {

        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize);
        Page<UserEntity> users = userRepository.findAll(pageable);
        List<UserResponseDTO> list = users.map(userMapper::toUserResponseDTO).stream().collect(Collectors.toList());
        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(users.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public void deleteUserById(UUID id) {

    }

    private void saveUserToken(UserEntity user, String jwtToken) {
        var token = TokenEntity.builder()
                .userEntity(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }


}
