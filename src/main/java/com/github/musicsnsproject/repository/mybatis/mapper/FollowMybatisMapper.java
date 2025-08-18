package com.github.musicsnsproject.repository.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.github.musicsnsproject.domain.follow.FollowVO;

@Mapper
public interface FollowMybatisMapper {

	// 나를 팔로우 하는 사람
	List<FollowVO> getFollowerList(@Param("userId") long userId);
}
