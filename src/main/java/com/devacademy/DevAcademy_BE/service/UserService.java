package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.userDTO.UserRequestDTO;
import com.devacademy.DevAcademy_BE.dto.userDTO.UserResponseDTO;
import com.devacademy.DevAcademy_BE.dto.userDTO.UserUpdateRequestDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public interface UserService {
    UserResponseDTO addUser(UserRequestDTO request);
    PageResponse<?> getAllUser(int page, int pageSize);
    void deleteUserById(UUID id);
    UserResponseDTO updateUserDTO(UUID id, UserUpdateRequestDTO request, MultipartFile file) throws IOException;
    UserResponseDTO getUserById(UUID id);
}
