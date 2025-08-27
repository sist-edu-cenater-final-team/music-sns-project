package com.github.musicsnsproject.web.controller.rest.comment;

import com.github.musicsnsproject.repository.jpa.community.comment.Comment;
import com.github.musicsnsproject.service.community.comment.CommentService;
import com.github.musicsnsproject.web.dto.comment.RequestCommentDTO;
import com.github.musicsnsproject.web.dto.comment.ResponseCommentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment/")
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;

    @GetMapping("getCommentList")
    public List<ResponseCommentDTO> getCommentList(@RequestParam("postId") Long postId){

        return commentService.getCommentList(postId);
    }

    @PostMapping("insertComment")
    public ResponseCommentDTO insertComment(@AuthenticationPrincipal Long userId,
                                                  @RequestBody RequestCommentDTO requestCommentDTO){

        return commentService.saveComments(userId, requestCommentDTO);

    }

}
