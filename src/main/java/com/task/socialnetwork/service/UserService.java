package com.task.socialnetwork.service;

import static com.task.socialnetwork.util.UserUtil.validateUserData;

import com.task.socialnetwork.dto.UserDTO;
import com.task.socialnetwork.dto.UserUpdateRequestDTO;
import com.task.socialnetwork.mapper.UserMapper;
import com.task.socialnetwork.model.User;
import com.task.socialnetwork.repository.CommentRepository;
import com.task.socialnetwork.repository.PostRepository;
import com.task.socialnetwork.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  @Transactional
  public UserDTO updateUserProfile(Long userId, UserUpdateRequestDTO updatedUser) {
    User existingUser = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    User user = userMapper.toEntity(updatedUser);

    validateUserData(user); // Validate input data

    existingUser.setUsername(updatedUser.getUsername());
    existingUser.setEmail(updatedUser.getEmail());

    // Update password only if provided
    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
      existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
    }

    userRepository.save(existingUser);

    return userMapper.toDTO(existingUser);
  }

  @Transactional
  public void deleteUser(Long userId) {
    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("User not found");
    }
    postRepository.deleteAllByUserId(userId);
    commentRepository.deleteAllByUserId(userId);
    userRepository.deleteById(userId);
  }

  public UserDTO getUserById(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    return userMapper.toDTO(user);
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }
}
