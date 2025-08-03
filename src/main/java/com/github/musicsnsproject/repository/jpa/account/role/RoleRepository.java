package com.github.musicsnsproject.repository.jpa.account.role;

import com.github.musicsnsproject.common.myenum.RolesEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(RolesEnum name);
}
