package com.ll.finalproject.app.base.initData;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.service.MemberService;
import com.ll.finalproject.app.post.service.PostService;

public interface InitDataBefore {
    default void before(MemberService memberService, PostService postService) {
        Member member1 = memberService.join("user1", "1234!","user1@test.com",null);
        Member member2 = memberService.join("user2", "1234!","user2@test.com",null);

        Member member3 = memberService.join("user3", "1234!","user3@test.com","유저3");
        Member member4 = memberService.join("user4", "1234!","user4@test.com","유저4");

        for (int i = 0; i < 110; i++) {
            postService.write(member1, "제목 " + i, "내용 " + i,null);
        }
    }
}