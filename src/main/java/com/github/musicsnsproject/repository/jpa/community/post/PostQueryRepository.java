package com.github.musicsnsproject.repository.jpa.community.post;

import com.github.musicsnsproject.web.dto.post.FollowPostVO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostQueryRepository  {

    List<FollowPostVO> findFollowPostByUserId(Long testUserId);

    Long findbyCntByPostId(List<Long> postIdForLikeCnt);
}
