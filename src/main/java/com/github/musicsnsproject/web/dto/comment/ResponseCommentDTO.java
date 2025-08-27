package com.github.musicsnsproject.web.dto.comment;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResponseCommentDTO {

    private Long commentId;
    private Long postId;
    private String contents;
    private String writer;
    private String writerProfileImageUrl;
    private LocalDateTime createdAt;


    private Long parentCommentId;

}
