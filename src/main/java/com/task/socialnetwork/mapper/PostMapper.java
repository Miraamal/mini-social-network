package com.task.socialnetwork.mapper;

import com.task.socialnetwork.dto.PostResponseDTO;
import com.task.socialnetwork.model.Post;
import java.util.Base64;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CommentMapper.class})
public interface PostMapper {
  PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

  @Mapping(target = "likeCount", expression = "java(post.getLikes().size())")
  @Mapping(source = "imageData", target = "imageData", qualifiedByName = "byteArrayToBase64")
  PostResponseDTO toResponseDTO(Post post);

  @Named("byteArrayToBase64")
  default String byteArrayToBase64(byte[] imageData) {
    return imageData != null ? Base64.getEncoder().encodeToString(imageData) : null;
  }
}