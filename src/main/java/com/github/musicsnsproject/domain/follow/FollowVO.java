package com.github.musicsnsproject.domain.follow;

import java.time.LocalDateTime;

import com.github.musicsnsproject.domain.user.MyUserVO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FollowVO {

	private long follower;
	private long followee;
	private boolean favorite;
	private LocalDateTime favoriteAt;
	private LocalDateTime createdAt;
	private boolean teist;
	private long teistCount;
	private MyUserVO user;
	

    public FollowVO(Long followee, Long follower, MyUserVO user, boolean teist) {
        this.followee = followee;
        this.follower = follower;
        this.user = user;
        this.teist = teist;
    }
    
    public FollowVO(Long teistCount, Long followee, MyUserVO user) {
        this.teistCount = teistCount;
        this.followee = followee;
        this.user = user;
    }
    
    
}
