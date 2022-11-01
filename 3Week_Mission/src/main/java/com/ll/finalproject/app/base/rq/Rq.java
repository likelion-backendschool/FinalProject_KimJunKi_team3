package com.ll.finalproject.app.base.rq;

import com.ll.finalproject.app.member.dto.MemberDto;
import com.ll.finalproject.app.security.dto.MemberContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
@RequestScope
public class Rq {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final MemberContext memberContext;
    @Getter
    private final MemberDto memberDto;

    public Rq(HttpServletRequest req, HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;

        // 현재 로그인한 회원의 인증정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("member = {}", authentication.getPrincipal());
        if (authentication.getPrincipal() instanceof MemberContext) {
            this.memberContext = (MemberContext) authentication.getPrincipal();
            this.memberDto = memberContext.getMemberDto();
        } else {
            this.memberContext = null;
            this.memberDto = null;
        }
    }

    public boolean hasAuthority(String authorityName) {
        if( memberContext == null ) return false;

        return memberContext.hasAuthority("ROLE_" + authorityName);
    }

    public long getId() {
        if (this.memberDto != null) {
            return memberDto.getId();
        }
        return 0;
    }
    public String getUsername() {
        if (this.memberDto != null) {
            return memberDto.getUsername();
        }
        return null;
    }

}

