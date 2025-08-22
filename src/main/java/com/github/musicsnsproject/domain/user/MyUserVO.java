package com.github.musicsnsproject.domain.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyUserVO {
    private long userId;
    private String nickname;
    private String username;
    private String email;
    private String profileMessage;
    private String profile_image;
    private long postCount;
    private long followeeCount;
    private long followerCount;


    public MyUserVO(long userId, String nickname, String email, String profile_image, String profileMessage) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.profile_image = profile_image;
        this.profileMessage = profileMessage;
    }


}
