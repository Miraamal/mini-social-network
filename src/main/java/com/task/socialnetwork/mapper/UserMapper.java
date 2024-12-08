package com.task.socialnetwork.mapper;

import com.task.socialnetwork.dto.RegistrationRequestDTO;
import com.task.socialnetwork.dto.UserDTO;
import com.task.socialnetwork.dto.UserUpdateRequestDTO;
import com.task.socialnetwork.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserDTO toDTO(User user);

  User toEntity(RegistrationRequestDTO requestDTO);

  User toEntity(UserUpdateRequestDTO requestDTO);
}