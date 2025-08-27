package com.github.musicsnsproject.web.dto.comment;

import lombok.Getter;

@Getter
public class RequestCommentDTO {

    private String comment;
    private Long postId;
    private Long parentCommentId;

}
