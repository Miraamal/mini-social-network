package com.task.socialnetwork.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDTO {
  private Long id;
  private String content;
  private String imageData; // Base64 encoded image
  private UserDTO user;
  private List<CommentDTO> comments;
  private List<UserDTO> likes; // Users who liked the post
  private LocalDateTime createdAt;
  private int likeCount;
}