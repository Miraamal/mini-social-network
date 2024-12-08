package com.task.socialnetwork.util;


import com.task.socialnetwork.model.User;

public class UserUtil {
  // Data validation for registration and profile updates
  public static void validateUserData(User user) {
    if (user.getUsername() == null || user.getUsername().isBlank()) {
      throw new IllegalArgumentException("Username cannot be blank");
    }
    if (user.getEmail() == null ||
        !user.getEmail().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
      throw new IllegalArgumentException("Invalid email format");
    }
    if (user.getPassword() != null && user.getPassword().length() < 6) {
      throw new IllegalArgumentException("Password must be at least 6 characters long");
    }
  }
}
