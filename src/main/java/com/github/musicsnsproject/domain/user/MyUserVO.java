package com.github.musicsnsproject.domain.user;

import lombok.Getter;

@Getter
public class MyUserVO {
    private long userId;
    private String nickname;
    private String email;

    private String profile_image;
    
    public MyUserVO(long userId, String nickname, String email, String profile_image) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.profile_image = profile_image;
    }


}
