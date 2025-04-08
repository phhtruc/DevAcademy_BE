package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.userDTO.UserRequestDTO;
import com.devacademy.DevAcademy_BE.dto.userDTO.UserResponseDTO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface UserService {

    UserResponseDTO getUserById(UUID id);
    UserResponseDTO addUser(UserRequestDTO request);
    PageResponse<?> getAllUser(int page, int pageSize);
    void deleteUserById(UUID id);
}
