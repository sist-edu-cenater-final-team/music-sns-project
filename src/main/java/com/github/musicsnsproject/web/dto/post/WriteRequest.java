package com.github.musicsnsproject.web.dto.post;

import com.github.musicsnsproject.common.myenum.EmotionEnum;
import com.github.musicsnsproject.web.dto.storage.FileDto;
import lombok.Getter;

import java.util.List;

@Getter
public class WriteRequest {
    private String title;
    private String contents;
    private EmotionEnum userEmotion;
    private List<FileDto> images;
}
