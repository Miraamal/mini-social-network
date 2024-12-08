package com.task.socialnetwork.mapper;

import com.task.socialnetwork.dto.AddCommentDTO;
import com.task.socialnetwork.dto.CommentDTO;
import com.task.socialnetwork.model.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {
  CommentDTO toDTO(Comment comment);

  Comment toEntity(AddCommentDTO comment);
}