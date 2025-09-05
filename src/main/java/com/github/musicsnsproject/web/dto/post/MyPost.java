package com.github.musicsnsproject.web.dto.post;

import com.github.musicsnsproject.common.myenum.EmotionEnum;
import com.github.musicsnsproject.repository.jpa.emotion.UserEmotion;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyPost {

    private Long postId;
    private EmotionEnum userEmotion;
    private String contents;
    private String title;
    private List<String> imageUrls;

}
