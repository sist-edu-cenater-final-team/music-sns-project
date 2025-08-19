package com.github.musicsnsproject.web.dto.post;

import com.github.musicsnsproject.repository.jpa.community.post.PostImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Service
@NoArgsConstructor
@AllArgsConstructor
public class FollowPostVO {

    private String title;
    private String contents;
    private String view_count;
    private Long shared_count;
    private List<PostImage> images;

}
