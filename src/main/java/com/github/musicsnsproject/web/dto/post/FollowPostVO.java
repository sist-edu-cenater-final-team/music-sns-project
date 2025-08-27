package com.github.musicsnsproject.web.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.musicsnsproject.common.myenum.EmotionEnum;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class FollowPostVO {

    private String username;
    private String profileImage;
    private String title;
    private String contents;
    private EmotionEnum emotionValue;
    private int postLikeCnt;
    private Long viewCount;
    private List<Long> likedUserPks;
    private Long postId;
    private Long userId;

    private List<String> post_image_urls;

    private boolean myLiked;


    public void liked(long loginUserId){
        this.myLiked = likedUserPks.contains(loginUserId);
        this.postLikeCnt = (int) likedUserPks.stream().distinct().count();
    }

    // Jackson이 JSON에 포함하도록 'get' 접두사를 사용
    // 필요 시 @JsonProperty로 이름을 명시할 수도 있습니다.
    @JsonProperty("emotionLabel")
    public String getEmotionLabel() {
        return emotionValue != null ? emotionValue.getValue() : "미정";
    }


}
