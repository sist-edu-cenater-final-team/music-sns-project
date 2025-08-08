package com.github.musicsnsproject.repository.jpa.account.history.login;

import java.time.LocalDateTime;

public interface LoginHistoryQueryRepository {
    LocalDateTime findLatestLoggedAtByUserId(Long userId);
}
