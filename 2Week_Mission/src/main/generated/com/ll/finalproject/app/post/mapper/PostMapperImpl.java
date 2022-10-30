package com.ll.finalproject.app.post.mapper;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.post.entity.Post;
import com.ll.finalproject.app.post.entity.PostDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-10-30T17:06:17+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.4.1 (Amazon.com Inc.)"
)
public class PostMapperImpl implements PostMapper {

    @Override
    public PostDto entityToPostDto(Post post) {
        if ( post == null ) {
            return null;
        }

        PostDto.PostDtoBuilder postDto = PostDto.builder();

        postDto.author( postAuthorUsername( post ) );
        postDto.id( post.getId() );
        postDto.subject( post.getSubject() );
        postDto.content( post.getContent() );
        postDto.contentHtml( post.getContentHtml() );
        postDto.createDate( post.getCreateDate() );
        postDto.modifyDate( post.getModifyDate() );

        postDto.postHashTagList( post.getPostKeyWordsFromPostHashTags() );

        return postDto.build();
    }

    @Override
    public List<PostDto> entitiesToPostDtos(List<Post> posts) {
        if ( posts == null ) {
            return null;
        }

        List<PostDto> list = new ArrayList<PostDto>( posts.size() );
        for ( Post post : posts ) {
            list.add( entityToPostDto( post ) );
        }

        return list;
    }

    private String postAuthorUsername(Post post) {
        if ( post == null ) {
            return null;
        }
        Member author = post.getAuthor();
        if ( author == null ) {
            return null;
        }
        String username = author.getUsername();
        if ( username == null ) {
            return null;
        }
        return username;
    }
}
