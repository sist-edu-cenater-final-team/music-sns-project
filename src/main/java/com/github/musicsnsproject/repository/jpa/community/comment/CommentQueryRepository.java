package com.github.musicsnsproject.repository.jpa.community.comment;

import com.github.musicsnsproject.web.dto.comment.ResponseCommentDTO;

import java.util.List;

public interface CommentQueryRepository {

    // 댓글 다 가져오기
    List<ResponseCommentDTO> findByCommentIdAndMyUser(Long postId);
}
