package com.github.musicsnsproject.web.controller.view.post;


import com.github.musicsnsproject.service.community.post.PostService;
import com.github.musicsnsproject.web.dto.post.FollowPostVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post/")
public class PostViewController {

    private final PostService postService;

    @GetMapping("/postView/{postId}")
    public String postViewByPath(@PathVariable Long postId) {

        //TODO:이렇게 보여주지 않고 업로드를 완료하면 바로 index 로 이동시킨다.

        return "post/postView";
    }

    @GetMapping("/postView")
    public String postView(@AuthenticationPrincipal Long userId,
                           HttpServletRequest request) {

        Long testUserId = 41L;
        List<FollowPostVO> followPostVOList = postService.followPostSelect(testUserId);

        List<Long> postIdForLikeCnt = followPostVOList.stream().map(FollowPostVO::getPostId).collect(Collectors.toList());
        Long n = postService.findbyCntByPostId(postIdForLikeCnt);

        // followPostVOList.forEach(vo -> vo.liked(testUserId));

        for(FollowPostVO followPostVO : followPostVOList){
            followPostVO.liked(testUserId);
        }

        request.setAttribute("n", n);
        request.setAttribute("testUserId", testUserId);
        request.setAttribute("followPostVOList", followPostVOList);

        return "post/postView";
    }


}
