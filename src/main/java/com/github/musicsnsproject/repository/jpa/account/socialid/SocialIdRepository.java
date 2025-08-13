package com.github.musicsnsproject.repository.jpa.account.socialid;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialIdRepository extends JpaRepository<SocialId, SocialIdPk>, SocialIdQueryRepository {
}
