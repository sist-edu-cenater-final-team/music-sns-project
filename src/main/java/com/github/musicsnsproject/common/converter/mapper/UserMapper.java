package com.github.musicsnsproject.common.converter.mapper;

import com.github.musicsnsproject.common.myenum.RoleEnum;
import com.github.musicsnsproject.common.security.userdetails.CustomUserDetails;
import com.github.musicsnsproject.config.client.oauth.dto.userinfo.OAuthUserInfo;
import com.github.musicsnsproject.repository.jpa.account.role.Role;
import com.github.musicsnsproject.repository.jpa.account.socialid.SocialId;
import com.github.musicsnsproject.repository.jpa.account.socialid.SocialIdPk;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import com.github.musicsnsproject.web.dto.account.auth.request.SignUpRequest;
import com.github.musicsnsproject.web.dto.account.auth.response.MyInfoResponse;
import com.github.musicsnsproject.web.dto.account.oauth.response.OAuthSignUpDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "roles", qualifiedByName = "getMyRoles")
    @Mapping(target = "gender", source = "myUser.gender")
    @Mapping(target = "dateOfBirth", dateFormat = "yyyy년 M월 d일")
    MyInfoResponse myUserToAccountDto(MyUser myUser);

    @Mapping(target = "dateOfBirth", dateFormat = "yyyy-M-d")
    @Mapping(target = "roles", ignore = true)
    MyUser accountDtoToMyUser(SignUpRequest signUpRequest);

    @Named("getMyRoles")
    default Set<RoleEnum> myRoles(Set<Role> roles){
        return roles.stream().map(r->r.getName())
                .collect(Collectors.toSet());
    }

////닉네임이 고유값이어야 할때    @Mapping(target = "nickname", expression = "java(oAuthInfoResponse.getNickName()+\"_\"+oAuthInfoResponse.getSocialId())")
    @Mapping(target = "password", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "status", constant = "TEMP")
    MyUser oAuthInfoResponseToMyUser(OAuthUserInfo oAuthUserInfo);

    @AfterMapping
    default void assignSocialId(OAuthUserInfo oAuthUserInfo, @MappingTarget MyUser myUser){
        SocialIdPk socialIdPk = SocialIdPk.of(oAuthUserInfo.getSocialId(), oAuthUserInfo.getOAuthProvider());
        SocialId newSocialId = SocialId.ofSocialIdPkAndMyUser(socialIdPk, myUser);
        myUser.addSocialId(newSocialId);
    }
    @Mapping(target = "provider", source = "OAuthProvider")
    OAuthSignUpDto oAuthUserInfoToOAuthSignUpDto(OAuthUserInfo oAuthUserInfo);


    @Mapping(target = "userId", source = "myUser.userId")
    @Mapping(target = "email", source = "myUser.email")
    @Mapping(target = "nickname", source = "myUser.nickname")
    @Mapping(target = "password", source = "myUser.password")
    @Mapping(target = "failureCount", source = "myUser.failureCount")
    @Mapping(target = "status", source = "myUser.status")
    @Mapping(target = "failureAt", source = "myUser.failureAt")
    @Mapping(target = "registeredAt", source = "myUser.registeredAt")
    @Mapping(target = "latestLoggedAt", source = "latestLoggedAt")
    @Mapping(target = "withdrawalAt", source = "myUser.withdrawalAt")
    @Mapping(target = "roles", source = "myUser.roles", qualifiedByName = "roleSetToRoleEnumSet")
    CustomUserDetails myUserToCustomUserDetails(MyUser myUser, LocalDateTime latestLoggedAt);
    @Named("roleSetToRoleEnumSet")
    default Set<RoleEnum> roleSetToRoleEnumSet(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }


}
