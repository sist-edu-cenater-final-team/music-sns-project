package com.github.musicsnsproject.domain.user;

import com.github.musicsnsproject.common.myenum.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyUserVO {
    private long userId;
    private String nickname;
    private String username;
    private String email;
    private String profileMessage;
    private String profile_image;
    private Gender gender;
    private boolean favorite;
    private long postCount;
    private long followeeCount;
    private long followerCount;
    private long coin;


    public MyUserVO(long userId, String nickname, String email, String profile_image, String profileMessage) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.profile_image = profile_image;
        this.profileMessage = profileMessage;
    }


}
