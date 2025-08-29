package com.github.musicsnsproject.repository.jpa.account.user;


import com.github.musicsnsproject.common.security.userdetails.CustomUserDetails;
import com.github.musicsnsproject.domain.PostVO;
import com.github.musicsnsproject.domain.user.MyUserVO;
import com.github.musicsnsproject.repository.jpa.account.socialid.SocialIdPk;
import com.github.musicsnsproject.web.dto.chat.ChatUserInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface MyUserQueryRepository {

    Optional<MyUser> findBySocialIdPkOrUserEmail(SocialIdPk socialIdPk, String email);

    Optional<CustomUserDetails> findByEmailOrPhoneNumberForAuth(String emailOrPhoneNumber);

    void updateFailureCountByEmail(CustomUserDetails failUser);

	List<PostVO> getUserPost(Long userId);
	// 유저 인포
	MyUserVO getUserInfo(Long userId);
	// 유저 인포 업데이트
	long updateUserInfo(Map<String, Object> paraMap);

    List<ChatUserInfo> findAllByIdForChatRoom(Set<Long> allOtherIds);
//    Optional<CustomUserDetails> findBySocialIdPkOrUserEmailForAuth(SocialIdPk socialIdPk, String email);
}
