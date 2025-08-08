package com.github.musicsnsproject.common.security.userdetails;

import com.github.musicsnsproject.common.myenum.RoleEnum;
import com.github.musicsnsproject.common.myenum.UserStatus;
import com.github.musicsnsproject.repository.jpa.account.user.MyUser;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Builder
public class CustomUserDetails implements UserDetails {
    private long userId;
    private String email;
    private String nickname;
    private String password;
    private int failureCount;
    private UserStatus status;
    private LocalDateTime failureAt;
    private LocalDateTime registeredAt;
    private LocalDateTime latestLoggedAt;
    private LocalDateTime withdrawalAt;
    private Set<RoleEnum> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return Long.toString(this.userId);
    }

    @Override
    public boolean isAccountNonExpired() {
        LocalDateTime base = (this.latestLoggedAt != null) ?
                this.latestLoggedAt : this.registeredAt;
        return base.isAfter(LocalDateTime.now().minusMonths(3));
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.status != UserStatus.LOCK  || isUnlockTime();
    }
    private boolean isUnlockTime() {
        return this.failureAt != null
                && this.failureAt.isBefore(LocalDateTime.now().minusMinutes(5));
    }
    public boolean isPasswordAuthFailure(PasswordEncoder passwordEncoder){
        return !passwordEncoder.matches(password, this.password);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return !isDisabled();
    }
    public boolean isDisabled() {
        return this.status == UserStatus.WITHDRAWAL || this.status == UserStatus.TEMP;
    }
    //로그인 실패 또는 성공시의 변화 될 값 DB에 반영 하기위한
    public void loginValueSetting(boolean failure) {
        //5번째 시도이고 5분이내 한번 더 시도했을시 잠금처리
        this.status = failure ?
                (isFailureCountingOrLocking() || isUnlockTime() ? UserStatus.NORMAL : UserStatus.LOCK)
                : UserStatus.NORMAL;
        //실패시 failureCount 를 1 증가시킨다. 단 계정이 잠길땐 0으로 만들고, 실패한지 5분 이상 지났을시 1부터 다시시작
        this.failureCount = failure ?
                (isUnlockTime() ?
                        1
                        : (isFailureCountingOrLocking() ? failureCount + 1 : 0))
                : 0;
        this.failureAt = failure ? LocalDateTime.now() : null;
        this.latestLoggedAt = !failure ? LocalDateTime.now() : this.latestLoggedAt;
    }
    public boolean isFailureCountingOrLocking() {
        return this.failureCount < 4;
    }

}
