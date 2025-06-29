package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.userDTO.*;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public interface UserService {

    UserResponseDTO addUser(AdminRequestDTO request, MultipartFile file) throws IOException, MessagingException;

    PageResponse<?> getAllUser(int page, int pageSize);

    void deleteUserById(UUID id);

    UserResponseDTO updateUserDTO(UUID id, UserUpdateRequestDTO request, MultipartFile file) throws IOException;

    UserResponseDTO getUserById(UUID id);

    UserResponseDTO setActive(UUID id);

    PageResponse<?> searchUser(@Valid UserSearchDTO searchDTO, int page, int pageSize);

    UserResponseDTO register(@Valid UserRequestDTO request);

    ProfileResponseDTO getProfile(Authentication authentication);
}
