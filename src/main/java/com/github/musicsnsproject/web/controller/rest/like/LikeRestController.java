package com.github.musicsnsproject.web.controller.rest.like;

import com.github.musicsnsproject.repository.jpa.community.like.LikeRepository;
import com.github.musicsnsproject.service.community.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like/")
public class LikeRestController {

    private final LikeService likeService;
    private final LikeRepository likeRepository;

    @PostMapping("goLike")
    public Map<String, Object> goLike(@RequestParam("postId") Long postId,
                                      @AuthenticationPrincipal Long userId){


        Long testUserId = 41L;

        boolean isExist = likeService.isLiked(postId, testUserId);


        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("isExist", isExist);

        return paraMap;
    }

}
