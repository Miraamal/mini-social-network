package com.task.socialnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserActivityDTO {
  private Long userId;
  private String username;
  private long postCount;
  private long likeCount;
  private long commentCount;
}
