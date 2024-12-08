package com.task.socialnetwork.service;

import com.task.socialnetwork.dto.PostStatisticsDTO;
import com.task.socialnetwork.dto.UserActivityDTO;
import com.task.socialnetwork.model.User;
import com.task.socialnetwork.repository.CommentRepository;
import com.task.socialnetwork.repository.PostRepository;
import com.task.socialnetwork.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatisticsService {
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final UserRepository userRepository;

  @Transactional
  public List<PostStatisticsDTO> getPopularPosts(int limit) {
    return postRepository.findAll().stream()
        .sorted((post1, post2) -> Integer.compare(post2.getLikeCount(), post1.getLikeCount()))
        .limit(limit)
        .map(post -> new PostStatisticsDTO(
            post.getId(),
            post.getContent(),
            post.getLikeCount(),
            post.getComments().size()
        ))
        .collect(Collectors.toList());
  }

  @Transactional
  public UserActivityDTO getUserActivity(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    long postCount = postRepository.countByUserId(user.getId());
    long likeCount = postRepository.findAllByUserId(user.getId()).stream()
        .mapToLong(post -> post.getLikes().size())
        .sum();
    long commentCount = commentRepository.countByUserId(user.getId());

    return new UserActivityDTO(user.getId(), user.getUsername(), postCount, likeCount,
        commentCount);
  }
}
