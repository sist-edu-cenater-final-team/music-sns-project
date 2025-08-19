package com.github.musicsnsproject.web.controller.rest.post;

import com.github.musicsnsproject.service.community.post.PostService;
import com.github.musicsnsproject.web.dto.post.WriteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/post/")
@RequiredArgsConstructor
public class PostRestController {
    private final PostService postService;

    @PostMapping("postTextAndTitle")
    public Long postOnlyTextAndTitle(@RequestBody WriteRequest request
                                    ,@AuthenticationPrincipal Long userId){
        return postService.writePostByRequest(request, userId);
    }

}
