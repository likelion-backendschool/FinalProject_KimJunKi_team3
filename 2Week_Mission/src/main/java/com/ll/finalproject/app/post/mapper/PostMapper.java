package com.ll.finalproject.app.post.mapper;

import com.ll.finalproject.app.post.entity.Post;
import com.ll.finalproject.app.post.entity.PostDto;
import com.ll.finalproject.app.post.hashTag.entity.PostHashTag;
import com.ll.finalproject.app.post.keyword.entity.PostKeyword;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(target = "postHashTagList", expression = "java(post.getPostKeyWordsFromPostHashTags())")
    @Mapping(source = "author.username", target = "author")
    PostDto entityToPostDto(Post post);
    List<PostDto> entitiesToPostDtos(List<Post> posts);

//    Post postDtoToEntity(PostDto postDto);
}