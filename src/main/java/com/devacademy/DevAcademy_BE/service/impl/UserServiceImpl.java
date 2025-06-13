package com.devacademy.DevAcademy_BE.service.impl;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.UserRegistrationTask;
import com.devacademy.DevAcademy_BE.dto.userDTO.*;
import com.devacademy.DevAcademy_BE.entity.RoleEntity;
import com.devacademy.DevAcademy_BE.entity.UserEntity;
import com.devacademy.DevAcademy_BE.entity.UserHasRoleEntity;
import com.devacademy.DevAcademy_BE.enums.ErrorCode;
import com.devacademy.DevAcademy_BE.enums.RegisterType;
import com.devacademy.DevAcademy_BE.enums.RoleType;
import com.devacademy.DevAcademy_BE.enums.UserStatus;
import com.devacademy.DevAcademy_BE.exception.ApiException;
import com.devacademy.DevAcademy_BE.mapper.UserMapper;
import com.devacademy.DevAcademy_BE.repository.CourseRegisterRepository;
import com.devacademy.DevAcademy_BE.repository.RoleRepository;
import com.devacademy.DevAcademy_BE.repository.UserHasRoleRepository;
import com.devacademy.DevAcademy_BE.repository.UserRepository;
import com.devacademy.DevAcademy_BE.repository.specification.UserSpecification;
import com.devacademy.DevAcademy_BE.service.*;
import com.devacademy.DevAcademy_BE.service.cloudinary.CloudinaryService;
import com.devacademy.DevAcademy_BE.service.queue.UserRegistrationQueueService;
import com.devacademy.DevAcademy_BE.service.token.TokenService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
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
    TokenService tokenService;
    JwtService jwtService;
    UserRegistrationQueueService userRegistrationQueueService;
    CourseRegisterRepository courseRegisterRepository;

    @Override
    public UserResponseDTO getUserById(UUID id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        var user = userMapper.toUserResponseDTO(userEntity);
        user.setRoles(userEntity.getAuthorities().toString());
        return user;

    }

    @Override
    public UserResponseDTO setActive(UUID id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        if (userEntity.getStatus() != UserStatus.INACTIVE) {
            userEntity.setStatus(UserStatus.INACTIVE);
        } else {
            userEntity.setStatus(UserStatus.ACTIVE);
        }
        userRepository.save(userEntity);
        tokenService.revokeAllUserTokens(userEntity.getId());
        return userMapper.toUserResponseDTO(userEntity);
    }

    @Override
    public PageResponse<?> searchUser(UserSearchDTO searchDTO, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize);

        Specification<UserEntity> spec = Specification
                .where(UserSpecification.hasNameOrEmail(searchDTO.getName()))
                .and(UserSpecification.hasStatus(searchDTO.getStatus()));

        Page<UserEntity> users = userRepository.findAll(spec, pageable);

        return getPageResponse(page, pageSize, users);
    }

    @Override
    @Transactional
    public UserResponseDTO register(UserRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorCode.EMAIL_EXISTS);
        }

        UserEntity user = userMapper.toUserEntity(request);
        user.setIsDeleted(false);
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        UserEntity savedUser = userRepository.save(user);

        RoleEntity resolvedRole = resolveRole(request.getRoles());
        associateRoleWithUser(savedUser, resolvedRole);

        UserResponseDTO userMap = userMapper.toUserResponseDTO(savedUser);
        userMap.setRoles(resolvedRole.getName().toString());

        return userMap;
    }

    @Override
    public ProfileResponseDTO getProfile(Authentication authentication) {
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        var user = userMapper.toProfileResponseDTO(userEntity);
        user.setTotalCourses(countUserBuyCourse(userEntity.getId()));
        return user;
    }

    private PageResponse<?> getPageResponse(int page, int pageSize, Page<UserEntity> users) {
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

    @Transactional
    @Override
    public UserResponseDTO addUser(AdminRequestDTO request, MultipartFile file) throws IOException, MessagingException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorCode.EMAIL_EXISTS);
        }

        UserEntity user = userMapper.toUserEntity(request);
        user.setIsDeleted(false);
        user.setStatus(UserStatus.PENDING);

        UserEntity savedUser = userRepository.saveAndFlush(user);

        RoleEntity resolvedRole = resolveRole(request.getRoles());
        associateRoleWithUser(savedUser, resolvedRole);

        String token = jwtService.generateToken(user, false);
        tokenService.saveToken(savedUser, token, 1440);

        if (file != null && !file.isEmpty()) {
            userRegistrationQueueService.addToQueue(new UserRegistrationTask(file, savedUser.getId(), token));
        } else {
            userRegistrationQueueService.addToQueue(new UserRegistrationTask(null, savedUser.getId(), token));
        }

        UserResponseDTO userMap = userMapper.toUserResponseDTO(savedUser);
        userMap.setRoles(resolvedRole.getName().toString());

        return userMap;
    }

    @Override
    public PageResponse<?> getAllUser(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, pageSize);
        Page<UserEntity> users = userRepository.findAll(pageable);

        return getPageResponse(page, pageSize, users);
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
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (request.getEmail() != null && !existingUser.getEmail().equals(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(ErrorCode.EMAIL_EXISTS);
        }

        if (request.getFullName() != null) {
            existingUser.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }
        if (request.getStatus() != null) {
            existingUser.setStatus(UserStatus.valueOf(request.getStatus().toUpperCase()));
        }
        if (request.getRoles() != null) {
            RoleEntity resolvedRole = resolveRole(request.getRoles());
            userHasRoleRepository.deleteAllByUserEntity_Id(id);
            associateRoleWithUser(existingUser, resolvedRole);
        }

        if (file != null && !file.isEmpty()) {
            existingUser.setAvatar(cloudinaryService.uploadImage(file));
        }

        return userMapper.toUserResponseDTO(userRepository.save(existingUser));
    }

    protected RoleEntity resolveRole(String roleName) {
        RoleType roleType;
        if (roleName == null) {
            roleType = RoleType.USER;
        } else {
            try {
                roleType = RoleType.valueOf(roleName.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ApiException(ErrorCode.ROLE_NAME_NOT_FOUND);
            }
        }
        return roleRepository.findByName(roleType)
                .orElseThrow(() -> new ApiException(ErrorCode.ROLE_NAME_NOT_FOUND));
    }

    protected void associateRoleWithUser(UserEntity user, RoleEntity role) {
        UserHasRoleEntity userHasRole = new UserHasRoleEntity();
        userHasRole.setUserEntity(user);
        userHasRole.setRoleEntity(role);

        userHasRoleRepository.save(userHasRole);
    }

    private Integer countUserBuyCourse(UUID uuid) {
        return courseRegisterRepository.countByUserEntityIdAndRegisterType(uuid, RegisterType.BUY);
    }

}
