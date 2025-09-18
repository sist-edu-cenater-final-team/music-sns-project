package com.github.musicsnsproject.repository.jpa.account.user;

import com.github.musicsnsproject.common.myenum.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.github.musicsnsproject.domain.user.MyUserVO;

import java.util.List;
import java.util.Optional;


@Repository
public interface MyUserRepository extends JpaRepository<MyUser, Long>, MyUserQueryRepository {
    Optional<MyUser> findByEmail(String email);
    Optional<MyUser> findByPhoneNumber(String phoneNumber);

    boolean existsByUserId_AndStatusIn(Long userId, List<UserStatus> status);

    @Query(
            "SELECT u " +
                    "FROM MyUser u " +
                    "JOIN FETCH u.roles r " +
                    "WHERE u.email = :email"
    )
    Optional<MyUser> findByEmailJoin(String email);
    @Query(
            "SELECT u " +
                    "FROM MyUser u " +
                    "JOIN FETCH u.roles r " +
                    "WHERE u.phoneNumber = ?1"
    )
    Optional<MyUser> findByPhoneNumberJoin(String phoneNumber);





    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByNickname(String nickname);
}
