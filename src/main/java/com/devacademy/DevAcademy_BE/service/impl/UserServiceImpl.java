package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.userDTO.UserRequestDTO;
import com.devacademy.DevAcademy_BE.dto.userDTO.UserResponseDTO;
import com.devacademy.DevAcademy_BE.dto.userDTO.UserUpdateRequestDTO;
import com.devacademy.DevAcademy_BE.entity.RoleEntity;
import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.entity.UserHasRoleEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.enums.RoleType;
import com.devacademy.DevAcademy_BE.enums.UserStatus;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.mapper.UserMapper;
import com.devacademy.DevAcademy_BE.repository.RoleRepository;
import com.devacademy.DevAcademy_BE.repository.UserHasRoleRepository;
import com.devacademy.DevAcademy_BE.repository.UserRepository;
import com.devacademy.DevAcademy_BE.service.CloudinaryService;
import com.devacademy.DevAcademy_BE.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    CloudinaryService cloudinaryService;

    @Override
    public UserResponseDTO getUserById(UUID id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        var user = userMapper.toUserResponseDTO(userEntity);
        user.setRoles(userEntity.getAuthorities().toString());
        return user;
    }

    @Transactional
    @Override
    public UserResponseDTO addUser(UserRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorCode.EMAIL_EXISTS);
        }

        UserEntity user = userMapper.toUserEntity(request);
        user.setIsDeleted(false);
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var savedUser = userRepository.save(user);
        RoleEntity userRole;
        if (request.getRoles() != null) {
            userRole = roleRepository.findByName(RoleType.TEACHER)
                    .orElseThrow(() -> new ApiException(ErrorCode.ROLE_NAME_NOT_FOUND));
        } else {
            userRole = roleRepository.findByName(RoleType.USER)
                    .orElseThrow(() -> new ApiException(ErrorCode.ROLE_NAME_NOT_FOUND));
        }

        UserHasRoleEntity userHasRole = new UserHasRoleEntity();
        userHasRole.setUserEntity(savedUser);
        userHasRole.setRoleEntity(userRole);
        userHasRoleRepository.save(userHasRole);

        var userMap = userMapper.toUserResponseDTO(savedUser);
        userMap.setRoles(userRole.getName().toString());
        return userMap;
    }

    @Override
    public PageResponse<?> getAllUser(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize);
        Page<UserEntity> users = userRepository.findAll(pageable);

        List<UserResponseDTO> list = users.stream()
                .map(userMap -> {
                    var user = userMapper.toUserResponseDTO(userMap);
                    user.setRoles(userMap.getAuthorities().toString());
                    return user;
                })
                .collect(Collectors.toList());

        return PageResponse.builder()
                .page(page)
                .pageSize(pageSize)
                .totalPage(users.getTotalPages())
                .items(list)
                .build();
    }

    @Override
    public void deleteUserById(UUID id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        user.setIsDeleted(true);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public UserResponseDTO updateUserDTO(UUID id, UserUpdateRequestDTO request, MultipartFile file) throws IOException {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        user.setFullName(request.getFullName());
        if(!file.isEmpty()) {
            user.setAvatar(cloudinaryService.uploadImage(file));
        }

        return userMapper.toUserResponseDTO(userRepository.save(user));
    }

}
