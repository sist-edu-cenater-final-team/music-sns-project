package com.github.musicsnsproject.repository.jpa.community.block;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockUserRepository extends JpaRepository<BlockUser, BlockUserPk>, BlockUserQueryRepository {

}
