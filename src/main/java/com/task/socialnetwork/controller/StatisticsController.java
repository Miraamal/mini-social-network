package com.task.socialnetwork.controller;

import com.task.socialnetwork.dto.PostStatisticsDTO;
import com.task.socialnetwork.dto.UserActivityDTO;
import com.task.socialnetwork.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

  private final StatisticsService statisticsService;

  @Operation(summary = "Retrieve popular posts",
      description = "Fetch the top posts based on likes and comments, limited by the provided number.")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  @GetMapping("/popular-posts")
  public ResponseEntity<List<PostStatisticsDTO>> getPopularPosts(
      @RequestParam(defaultValue = "10") int limit) {
    List<PostStatisticsDTO> popularPosts = statisticsService.getPopularPosts(limit);
    return ResponseEntity.ok(popularPosts);
  }

  @Operation(summary = "Retrieve user activity statistics",
      description = "Fetch user activity statistics, including post count, like count, and comment count.")
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/user-activity")
  public ResponseEntity<UserActivityDTO> getUserActivity(@RequestParam Long userId) {
    UserActivityDTO userActivity = statisticsService.getUserActivity(userId);
    return ResponseEntity.ok(userActivity);
  }
}
