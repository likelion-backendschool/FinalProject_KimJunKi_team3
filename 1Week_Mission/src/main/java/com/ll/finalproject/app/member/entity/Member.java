package com.ll.finalproject.app.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.finalproject.app.base.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;

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
    private String nickname;

    public void changeEmailAndNickname(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}
