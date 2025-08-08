package com.github.musicsnsproject.repository.jpa.account.role;

import com.github.musicsnsproject.common.myenum.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(RoleEnum name);

}
