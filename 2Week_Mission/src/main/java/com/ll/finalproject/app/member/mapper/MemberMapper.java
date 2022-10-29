package com.ll.finalproject.app.member.mapper;

import com.ll.finalproject.app.member.dto.MemberDto;
import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.dto.MemberJoinForm;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemberMapper {
    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    Member memberJoinFormToEntity(MemberJoinForm memberJoinForm);
    MemberJoinForm entityToMemberJoinForm(Member member);

    MemberDto entityToMemberDto(Member member);
}
