package com.devacademy.DevAcademy_BE.mapper;

import com.devacademy.DevAcademy_BE.dto.userDTO.*;
import com.devacademy.DevAcademy_BE.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toUserEntity(UserRequestDTO userRequestDTO);

    UserResponseDTO toUserResponseDTO(UserEntity userEntity);

    UserEntity toUserEntityByUpdate(UserUpdateRequestDTO userRequestDTO);

    UserEntity toUserEntity(AdminRequestDTO userRequestDTO);

    ProfileResponseDTO toProfileResponseDTO(UserEntity userEntity);
}
