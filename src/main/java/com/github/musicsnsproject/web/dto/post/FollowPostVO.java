package com.github.musicsnsproject.web.dto.post;

import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class FollowPostVO {

    private String username;
    private String profileImage;
    private String title;
    private String contents;
    private Long viewCount;
    private List<Long> likedUserPks;
    private Long postId;
    private Long userId;

    private List<String> post_image_urls;

    private boolean myLiked;


    public void liked(long loginUserId){
        this.myLiked = likedUserPks.contains(loginUserId);
    }


}
