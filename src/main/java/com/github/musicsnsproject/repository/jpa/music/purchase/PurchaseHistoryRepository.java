package com.github.musicsnsproject.repository.jpa.music.purchase;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long>, PurchaseHistoryQueryRepository {
}
