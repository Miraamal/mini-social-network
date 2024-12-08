package com.task.socialnetwork.repository;

import com.task.socialnetwork.model.Post;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  // Use an EntityGraph to eagerly load comments and likes
  @EntityGraph(attributePaths = {"comments", "likes"})
  @Query("SELECT p FROM Post p WHERE p.id = :id")
  Optional<Post> findByIdWithCommentsAndLikes(Long id);

  List<Post> findAllByUserId(Long userId);

  @Query("SELECT p FROM Post p LEFT JOIN p.likes l GROUP BY p ORDER BY COUNT(l) DESC")
  List<Post> findAllByPopularity();

  @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
  List<Post> findAllByTime();

  long countByUserId(Long userId);
  void deleteAllByUserId(Long userId);
}
