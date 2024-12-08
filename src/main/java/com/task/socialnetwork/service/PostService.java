package com.task.socialnetwork.service;

import com.task.socialnetwork.dto.AddCommentDTO;
import com.task.socialnetwork.dto.CommentDTO;
import com.task.socialnetwork.dto.PostResponseDTO;
import com.task.socialnetwork.error.UnauthorizedException;
import com.task.socialnetwork.mapper.CommentMapper;
import com.task.socialnetwork.mapper.PostMapper;
import com.task.socialnetwork.model.Comment;
import com.task.socialnetwork.model.FilterType;
import com.task.socialnetwork.model.Post;
import com.task.socialnetwork.model.User;
import com.task.socialnetwork.repository.CommentRepository;
import com.task.socialnetwork.repository.PostRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final PostMapper postMapper;
  private final CommentMapper commentMapper;

  @Transactional
  public PostResponseDTO createPost(String content, byte[] imageData, User user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    Post post = new Post();
    post.setContent(content);
    post.setImageData(imageData);
    post.setUser(user); // Set the user explicitly
    postRepository.save(post);
    return postMapper.toResponseDTO(post);
  }

  @Transactional
  public List<PostResponseDTO> getNewsFeed(FilterType filter) {
    switch (filter) {
      case POPULARITY:
        return postRepository.findAllByPopularity()
            .stream()
            .map(postMapper::toResponseDTO)
            .toList();
      case TIME:
      default:
        return postRepository.findAllByTime()
            .stream()
            .map(postMapper::toResponseDTO)
            .toList();
    }
  }

  @Transactional
  public void likePost(Long postId, User user) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    if (post.getLikes().contains(user)) {
      post.removeLike(user);
    } else {
      post.addLike(user);
    }
    postRepository.save(post);
  }

  @Transactional
  public CommentDTO addComment(Long postId, AddCommentDTO commentDto, User user) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

    Comment comment = commentMapper.toEntity(commentDto);

    comment.setPost(post);
    comment.setUser(user);
    comment.setCreatedAt(LocalDateTime.now());
    commentRepository.save(comment);

    return commentMapper.toDTO(comment);
  }

  @Transactional
  public PostResponseDTO getPostById(Long postId) {
    Post post = postRepository.findByIdWithCommentsAndLikes(postId)
        .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    return postMapper.toResponseDTO(post);

  }

  @Transactional
  public List<PostResponseDTO> getAllPosts() {
    return postRepository.findAll().stream()
        .map(postMapper::toResponseDTO)
        .toList();
  }

  @Transactional
  public PostResponseDTO updatePost(Long postId, String content, byte[] imageData, User user) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    if (!post.getUser().equals(user)) {
      throw new UnauthorizedException("You are not authorized to update this post");
    }
    post.setContent(content);
    if (imageData != null) {
      post.setImageData(imageData);
    }
    return PostMapper.INSTANCE.toResponseDTO(postRepository.save(post));
  }

  @Transactional
  public void deletePost(Long postId) {
    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    postRepository.delete(post);
  }

  public boolean isPostOwner(Long postId, Long userId) {
    return postRepository.findById(postId).get().getUser().getId().equals(userId);
  }

  public List<PostResponseDTO> getPostsByUser(Long userId, FilterType filter,
                                              LocalDateTime startTime, LocalDateTime endTime) {
    List<Post> posts = postRepository.findAllByUserId(userId);

    // Filter by start and end time if provided
    if (startTime != null) {
      posts = posts.stream()
          .filter(post -> !post.getCreatedAt().isBefore(startTime))
          .collect(Collectors.toList());
    }
    if (endTime != null) {
      posts = posts.stream()
          .filter(post -> !post.getCreatedAt().isAfter(endTime))
          .collect(Collectors.toList());
    }

    // Sort the posts based on filter criteria
    if (FilterType.POPULARITY.equals(filter)) {
      posts.sort((p1, p2) -> Integer.compare(p2.getLikeCount(), p1.getLikeCount()));
    } else {
      // Default sorting by time (descending)
      posts.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
    }

    return posts.stream()
        .map(postMapper::toResponseDTO)
        .toList();
  }
}