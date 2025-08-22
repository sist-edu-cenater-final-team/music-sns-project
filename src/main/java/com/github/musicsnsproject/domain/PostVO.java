package com.github.musicsnsproject.domain;

import java.util.List;

import lombok.Getter;

@Getter
public class PostVO {

	private long userId;
	private long postId;
	private String title;
	private List<String> postImageUrl;
	
}
