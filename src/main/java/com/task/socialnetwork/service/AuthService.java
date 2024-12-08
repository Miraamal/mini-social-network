package com.task.socialnetwork.service;

import static com.task.socialnetwork.util.UserUtil.validateUserData;

import com.task.socialnetwork.dto.RegistrationRequestDTO;
import com.task.socialnetwork.mapper.UserMapper;
import com.task.socialnetwork.model.Role;
import com.task.socialnetwork.model.User;
import com.task.socialnetwork.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  public void registerUser(RegistrationRequestDTO requestDTO) {
    User user = userMapper.toEntity(requestDTO);
    validateUserData(user);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setRole(Role.ROLE_USER);
    userRepository.save(user);
  }

}