package com.github.musicsnsproject.web.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ResponseCommentDTO {

    private Long commentId;
    private Long postId;
    private String contents;
    private String writer;
    private String writerProfileImageUrl;
    private LocalDateTime createdAt;


    private Long parentCommentId;

    private Long replyCount;


}
