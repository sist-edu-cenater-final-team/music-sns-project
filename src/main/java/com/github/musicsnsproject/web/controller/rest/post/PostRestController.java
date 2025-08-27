package com.github.musicsnsproject.web.controller.rest.post;

import com.github.musicsnsproject.common.myenum.EmotionEnum;
import com.github.musicsnsproject.service.community.post.PostService;
import com.github.musicsnsproject.web.dto.post.FollowPostVO;
import com.github.musicsnsproject.web.dto.post.WriteRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


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


    @GetMapping("postView")
    public List<FollowPostVO> postView(@AuthenticationPrincipal Long userId) {

        List<FollowPostVO> followPostVOList = postService.followPostSelect(userId);

        List<Long> postIdForLikeCnt = followPostVOList.stream().map(FollowPostVO::getPostId).collect(Collectors.toList());

        // followPostVOList.forEach(vo -> vo.liked(testUserId));


        for(FollowPostVO followPostVO : followPostVOList){
            followPostVO.liked(userId);

        }

        return followPostVOList;
    }

}
