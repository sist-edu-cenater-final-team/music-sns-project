package com.github.musicsnsproject.domain.follow;

import java.time.LocalDateTime;

import com.github.musicsnsproject.domain.user.MyUserVO;

import lombok.Getter;

@Getter
public class FollowVO {

	private long follower;
	private long followee;
	private boolean favorite;
	private LocalDateTime favoriteAt;
	private LocalDateTime createdAt;

	private MyUserVO user;
	

    public FollowVO(Long followee, Long follower, MyUserVO user) {
        this.followee = followee;
        this.follower = follower;
        this.user = user;
    }
}
