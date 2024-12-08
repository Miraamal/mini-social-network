package com.task.socialnetwork.controller;

import com.task.socialnetwork.dto.AddCommentDTO;
import com.task.socialnetwork.dto.CommentDTO;
import com.task.socialnetwork.dto.PostResponseDTO;
import com.task.socialnetwork.model.CustomUserDetails;
import com.task.socialnetwork.model.FilterType;
import com.task.socialnetwork.model.User;
import com.task.socialnetwork.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
  private final PostService postService;

  @Operation(summary = "Create a new post",
      description = "Allows a user to create a new post with optional image upload.")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<PostResponseDTO> createPost(
      @RequestParam("content") String content,
      @RequestParam(value = "image", required = false)
      @Parameter(description = "Upload an image file",
          content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
      MultipartFile imageFile,
      @AuthenticationPrincipal CustomUserDetails customUserDetails) {
    User user = customUserDetails.getUser();
    try {
      byte[] imageData = imageFile != null ? imageFile.getBytes() : null;
      PostResponseDTO createdPost = postService.createPost(content, imageData, user);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
  }

  @Operation(summary = "Retrieve a post by ID",
      description = "Fetch the details of a specific post using its ID.")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  @GetMapping("/{postId}")
  public ResponseEntity<PostResponseDTO> getPost(@PathVariable Long postId) {
    PostResponseDTO post = postService.getPostById(postId);
    return ResponseEntity.ok(post);
  }

  @Operation(summary = "Like or unlike a post",
      description = "Allows a user to like or unlike a specific post.")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  @PostMapping("/{postId}/like")
  public ResponseEntity<Void> likePost(@PathVariable Long postId,
                                       @AuthenticationPrincipal
                                       CustomUserDetails customUserDetails) {
    User user = customUserDetails.getUser();
    postService.likePost(postId, user);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Comment on a post",
      description = "Allows a user to add a comment to a specific post.")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  @PostMapping("/{postId}/comments")
  public ResponseEntity<CommentDTO> addComment(@PathVariable Long postId,
                                               @RequestBody AddCommentDTO comment,
                                               @AuthenticationPrincipal
                                               CustomUserDetails customUserDetails) {
    User user = customUserDetails.getUser();
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(postService.addComment(postId, comment, user));
  }

  @Operation(summary = "Retrieve news feed",
      description = "Fetch the latest posts with an optional filter by time or popularity.")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  @GetMapping("/feed")
  public ResponseEntity<List<PostResponseDTO>> getNewsFeed(
      @RequestParam FilterType filter) {
    return ResponseEntity.ok(postService.getNewsFeed(filter));
  }

  @Operation(summary = "Retrieve all posts",
      description = "Fetch a list of all posts available in the system.")
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
    List<PostResponseDTO> posts = postService.getAllPosts();
    return ResponseEntity.ok(posts);
  }

  @Operation(summary = "Update a post",
      description = "Allows a user to update the content or image of their post.")
  @PreAuthorize("postService.isPostOwner(#postId, #customUserDetails.user.id) or hasRole('ADMIN')")
  @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long postId,
                                                    @RequestParam("content") String content,
                                                    @RequestParam(value = "image", required = false)
                                                    @Parameter(description = "Upload an image file",
                                                        content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
                                                    MultipartFile imageFile,
                                                    @AuthenticationPrincipal
                                                    CustomUserDetails customUserDetails) {
    User user = customUserDetails.getUser();
    try {
      byte[] imageData = imageFile != null ? imageFile.getBytes() : null;
      PostResponseDTO updatedPost = postService.updatePost(postId, content, imageData, user);
      return ResponseEntity.ok(updatedPost);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
  }

  @Operation(summary = "Delete a post",
      description = "Allows a user to delete their specific post.")
  @PreAuthorize("hasRole('ADMIN') or postService.isPostOwner(#postId, #customUserDetails.user.id)")
  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(@PathVariable Long postId,
                                         @AuthenticationPrincipal
                                         CustomUserDetails customUserDetails) {
    postService.deletePost(postId);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Get all posts by a user",
      description = "Allows an admin or the user to view all posts by a specific user.")
  @PreAuthorize("hasRole('ADMIN') or #userId == #customUserDetails.user.id")
  @GetMapping("/user/{userId}/filtered")
  public ResponseEntity<List<PostResponseDTO>> getFilteredPostsByUser(
      @PathVariable Long userId,
      @RequestParam FilterType filter,
      @RequestParam(required = false) LocalDateTime startTime,
      @RequestParam(required = false) LocalDateTime endTime,
      @AuthenticationPrincipal
      CustomUserDetails customUserDetails) {
    List<PostResponseDTO> userPosts =
        postService.getPostsByUser(userId, filter, startTime, endTime);
    return ResponseEntity.ok(userPosts);
  }
}