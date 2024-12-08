package com.task.socialnetwork.repository;

import com.task.socialnetwork.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
  long countByUserId(Long userId);
  void deleteAllByUserId(Long userId);
}