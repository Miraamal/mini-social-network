package com.task.socialnetwork.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
  private Long id;
  private String content;
  private UserDTO user;
  private LocalDateTime createdAt;
}