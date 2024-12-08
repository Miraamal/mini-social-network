package com.task.socialnetwork.controller;

import com.task.socialnetwork.dto.UserDTO;
import com.task.socialnetwork.dto.UserUpdateRequestDTO;
import com.task.socialnetwork.model.CustomUserDetails;
import com.task.socialnetwork.model.User;
import com.task.socialnetwork.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @Operation(summary = "Update user profile",
      description = "Allows a user to update their profile details.")
  @PreAuthorize("#userId == #customUserDetails.user.id or hasRole('ADMIN')")
  @PutMapping("/{userId}")
  public ResponseEntity<UserDTO> updateUserProfile(
      @PathVariable Long userId,
      @RequestBody UserUpdateRequestDTO updatedUser,
      @AuthenticationPrincipal
      CustomUserDetails customUserDetails) {
    return ResponseEntity.ok(userService.updateUserProfile(userId, updatedUser));
  }

  @Operation(summary = "Delete a user",
      description = "Allows a user to delete their own account.")
  @PreAuthorize("#userId == #customUserDetails.user.id or hasRole('ADMIN')")
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(
      @PathVariable Long userId,
      @AuthenticationPrincipal
      CustomUserDetails customUserDetails) {
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Retrieve a user by ID",
      description = "Fetch the details of a specific user using their ID.")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  @GetMapping("/{userId}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
    return ResponseEntity.ok(userService.getUserById(userId));
  }

  @Operation(summary = "Retrieve all users",
      description = "Fetch a list of all registered users in the system.")
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<List<User>> getAllUsers() {
    List<User> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }
}