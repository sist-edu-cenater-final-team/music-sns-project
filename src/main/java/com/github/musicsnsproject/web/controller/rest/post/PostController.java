package com.github.musicsnsproject.web.controller.rest.post;

import com.github.musicsnsproject.service.community.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post/")
public class PostController {

    private final PostService postService;

    // 1) 페이지(뷰) 반환: /post/postEdit
    @GetMapping("postEdit")
    public String postEditPage() {
        // JSP는 빈 껍데기. 데이터는 JS에서 /api로 따로 조회
        return "post/postEdit";
    }

}
