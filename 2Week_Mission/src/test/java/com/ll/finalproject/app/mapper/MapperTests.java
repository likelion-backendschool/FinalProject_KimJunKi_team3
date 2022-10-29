package com.ll.finalproject.app.mapper;

import com.ll.finalproject.app.member.entity.Member;
import com.ll.finalproject.app.member.dto.MemberJoinForm;
import com.ll.finalproject.app.member.mapper.MemberMapper;
import groovy.util.logging.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles({"test", "base"})
public class MapperTests {

    @Test
    public void memberToMemberJoinForm() {
        //given
        Member member = Member.builder()
                .username("hello123")
                .email("hello123@test.com")
                .password("123")
                .build();

        //when
        MemberJoinForm memberJoinForm = MemberMapper.INSTANCE.entityToMemberJoinForm(member);

        System.out.println("member = " + member);
        System.out.println("memberJoinForm = " + memberJoinForm);

        //then
        assertThat( memberJoinForm ).isNotNull();
        assertThat( memberJoinForm.getUsername() ).isEqualTo( "hello123" );
        assertThat( memberJoinForm.getEmail() ).isEqualTo( "hello123@test.com" );
        assertThat( memberJoinForm.getPassword() ).isEqualTo( "123" );
        assertThat( memberJoinForm.getPasswordConfirm() ).isNull();

    }

}
