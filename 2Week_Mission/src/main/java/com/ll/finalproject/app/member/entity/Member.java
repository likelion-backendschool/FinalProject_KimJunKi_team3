package com.ll.finalproject.app.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.finalproject.app.base.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Member extends BaseEntity {
    @Column(unique = true)
    private String username;
    @JsonIgnore
    private String password;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String nickname;
    private int authLevel;
    private long restCash; // 남은 캐시

    public void changeEmailAndNickname(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeEmail(String email) {
        this.email = email;
    }
    public void changeRestCash(long restCash) {
        this.restCash = restCash;
    }
    public List<GrantedAuthority> genAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));

        // 닉네임을 가지고 있다면 작가의 권한을 가진다.
        if (StringUtils.hasText(nickname)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_AUTHOR"));
        }

        return authorities;
    }
}
