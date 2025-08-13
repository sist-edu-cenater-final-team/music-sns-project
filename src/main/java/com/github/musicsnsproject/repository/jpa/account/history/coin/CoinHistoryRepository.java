package com.github.musicsnsproject.repository.jpa.account.history.coin;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinHistoryRepository extends JpaRepository<CoinHistory, Long>, CoinHistoryQueryRepository {

}
