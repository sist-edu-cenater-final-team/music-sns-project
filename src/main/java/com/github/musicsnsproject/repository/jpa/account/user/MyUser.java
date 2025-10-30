package com.github.musicsnsproject.repository.jpa.account.user;

import com.github.musicsnsproject.common.converter.custom.GenderConverter;
import com.github.musicsnsproject.common.converter.custom.UserStatusConverter;
import com.github.musicsnsproject.common.myenum.Gender;
import com.github.musicsnsproject.common.myenum.RoleEnum;
import com.github.musicsnsproject.common.myenum.UserStatus;
import com.github.musicsnsproject.repository.jpa.account.follow.Follow;
import com.github.musicsnsproject.repository.jpa.account.role.Role;
import com.github.musicsnsproject.repository.jpa.account.socialid.SocialId;
import com.github.musicsnsproject.repository.jpa.account.history.login.LoginHistory;
import com.github.musicsnsproject.repository.jpa.emotion.Emotion;
import com.github.musicsnsproject.repository.jpa.emotion.UserEmotion;
import com.github.musicsnsproject.web.dto.account.oauth.response.OAuthSignUpDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@DynamicInsert
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor
//@Check(constraints = "gender IN ('남성','여성','미정') AND status IN ('정상 계정','임시 계정','잠긴 계정','탈퇴 계정')")
public class MyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 30, unique = true)
    private String nickname;

    @Column(length = 30, nullable = false)
    private String username;

    @Column(length = 15, unique = true)
    private String phoneNumber;

    @Column(length = 50, unique = true)
    private String email;

    private String password;


    @Convert(converter = GenderConverter.class)
    private Gender gender;

    private LocalDate dateOfBirth;

    @Column(length = 500)
    private String profileImage;
    @Column(length = 255)
    private String profileMessage;

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    @Column(nullable = false, length = 15)
    @Convert(converter = UserStatusConverter.class)
    private UserStatus status;

    @Column(nullable = false)
    private int failureCount;
    @Column(nullable = false)
    private long coin;

    private LocalDateTime failureAt;

    private LocalDateTime withdrawalAt;

    @OneToMany(mappedBy = "myUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LoginHistory> loginHistories;

    @OneToMany(mappedBy = "myUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserEmotion> userEmotions;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_emotions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "emotion_id"))
    private List<Emotion> emotions;


    @OneToMany(mappedBy = "myUser", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<SocialId> socialIds;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    // 내가 팔로우한 사람들 (내가 follower)
    @OneToMany(mappedBy = "followPk.follower", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Follow> following = new ArrayList<>();

    // 나를 팔로우한 사람들 (내가 followee)
    @OneToMany(mappedBy = "followPk.followee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Follow> followers = new ArrayList<>();


    public static MyUser onlyId(Long userId) {
        MyUser myUser = new MyUser();
        myUser.userId = userId;
        return myUser;
    }

    public boolean isLocked() {
        return this.status == UserStatus.LOCK;
    }

    public boolean isTempAccount() {
        return this.status == UserStatus.TEMP;
    }

    public boolean isDisabled() {
        return this.status == UserStatus.WITHDRAWAL || this.status == UserStatus.TEMP;
    }

    private LocalDateTime getLastLogin() {
        return this.loginHistories.stream()
                .map(LoginHistory::getLoggedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    public boolean isCredentialsExpired() {
        return false;
    }

    public boolean isEnabled() {
        return this.status == UserStatus.NORMAL;
    }

    public boolean isUnlockTime() {
        return this.failureAt != null
                && this.failureAt.isBefore(LocalDateTime.now().minusMinutes(5));
    }

    public boolean isFailureCountingOrLocking() {
        return this.failureCount < 4;
    }


    public void oAuthSignUpSetting(OAuthSignUpDto oAuthSignUpDto) {
        this.email = oAuthSignUpDto.getEmail();
        this.nickname = oAuthSignUpDto.getNickname();
        this.status = UserStatus.NORMAL;
        this.phoneNumber = oAuthSignUpDto.getPhoneNumber();
        if (oAuthSignUpDto.getDateOfBirth() != null)
            this.dateOfBirth = LocalDate.parse(oAuthSignUpDto.getDateOfBirth(), DateTimeFormatter.ofPattern("yyyy-M-d"));
        this.setAsDefaultOrUpdate(oAuthSignUpDto.getProfileImage(), oAuthSignUpDto.getGender());
    }

    private void setAsDefaultOrUpdate(String profileImg, Gender gender) {
        this.gender = gender != null ?
                gender : Gender.UNKNOWN;
        this.profileImage = profileImg != null ?
                profileImg : Gender.UNKNOWN.getDefaultProfileImgUrl();
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles().stream()
                .map(roles -> new SimpleGrantedAuthority(roles.getName().name()))
                .collect(Collectors.toSet());
    }

    public void addSocialId(SocialId socialId) {
        if (this.socialIds == null) this.socialIds = Set.of(socialId);
        else this.socialIds.add(socialId);
    }

    public void setBeginRole(RoleEnum beginRoleName) {
        this.roles = Set.of(Role.fromName(beginRoleName));
    }
    public void setDefaultImg(){
        this.profileImage = "/images/profile/default-profile.png";
    }

    public void updateProfileImgFromOAuthInfo(String oauthProfileImage) {
        if (oauthProfileImage != null && Gender.isDefaultProfileImg(this.profileImage))
            this.profileImage = oauthProfileImage;

    }


    public void setBeginOAuthInfo() {
        this.setBeginRole(RoleEnum.ROLE_USER);
        this.setDefaultImg();
        this.gender = Gender.UNKNOWN;
    }
}
