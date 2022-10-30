package com.ll.finalproject.app.member.mapper;

import com.ll.finalproject.app.member.dto.MemberDto;
import com.ll.finalproject.app.member.dto.MemberJoinForm;
import com.ll.finalproject.app.member.entity.Member;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-10-30T12:05:54+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.4.1 (Amazon.com Inc.)"
)
public class MemberMapperImpl implements MemberMapper {

    @Override
    public Member memberJoinFormToEntity(MemberJoinForm memberJoinForm) {
        if ( memberJoinForm == null ) {
            return null;
        }

        Member.MemberBuilder<?, ?> member = Member.builder();

        member.username( memberJoinForm.getUsername() );
        member.password( memberJoinForm.getPassword() );
        member.email( memberJoinForm.getEmail() );

        return member.build();
    }

    @Override
    public MemberJoinForm entityToMemberJoinForm(Member member) {
        if ( member == null ) {
            return null;
        }

        MemberJoinForm memberJoinForm = new MemberJoinForm();

        memberJoinForm.setUsername( member.getUsername() );
        memberJoinForm.setPassword( member.getPassword() );
        memberJoinForm.setEmail( member.getEmail() );

        return memberJoinForm;
    }

    @Override
    public MemberDto entityToMemberDto(Member member) {
        if ( member == null ) {
            return null;
        }

        MemberDto.MemberDtoBuilder memberDto = MemberDto.builder();

        memberDto.id( member.getId() );
        memberDto.username( member.getUsername() );
        memberDto.nickname( member.getNickname() );
        memberDto.email( member.getEmail() );
        memberDto.createDate( member.getCreateDate() );
        memberDto.modifyDate( member.getModifyDate() );

        return memberDto.build();
    }
}
