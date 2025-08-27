package com.github.musicsnsproject.repository.jpa.music.purchase;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseMusicRepository extends JpaRepository<PurchaseMusic, Long>, PurchaseMusicQueryRepository {


}
