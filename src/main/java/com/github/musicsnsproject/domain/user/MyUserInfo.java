package com.github.musicsnsproject.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyUserInfo {

	private MyUserVO myuser;
	private boolean isOwner;
}
