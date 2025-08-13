package com.github.musicsnsproject.repository.jpa.account.socialid;

import java.util.Optional;

public interface SocialIdQueryRepository {

    Optional<SocialId> findBySocialIdPkJoinMyUser(SocialIdPk socialIdPk);
}
