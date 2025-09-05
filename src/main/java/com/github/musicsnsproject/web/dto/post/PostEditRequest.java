package com.github.musicsnsproject.web.dto.post;

import com.github.musicsnsproject.common.myenum.EmotionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class PostEditRequest {

    private Long postId;
    private String title;
    private String contents;
    private EmotionEnum userEmotion;

}
