package com.devacademy.DevAcademy_BE.service;

import com.devacademy.DevAcademy_BE.dto.PageResponse;
import com.devacademy.DevAcademy_BE.dto.userDTO.UserRequestDTO;
import com.devacademy.DevAcademy_BE.dto.userDTO.UserResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public interface UserService {

    UserResponseDTO getUserById(UUID id);

    UserResponseDTO addUser(UserRequestDTO request);
//
//    UserResponseDTO updateUser(UUID id, UserRequestDTO request);
//
//    void deleteUser(UUID id);

    PageResponse<?> getAllUser(int page, int pageSize);

//    UserResponseDTO2 createUser(UserRequestDTO2 request, MultipartFile multipartFile);
//
//    UserResponseDTO2 updateUserDTO(UUID id, UserRequestDTO2 request);

    void deleteUserById(UUID id);
}
