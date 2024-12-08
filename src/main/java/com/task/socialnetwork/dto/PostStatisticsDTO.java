package com.task.socialnetwork.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostStatisticsDTO {
  private Long postId;
  private String content;
  private long likeCount;
  private long commentCount;
}
