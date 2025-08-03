package com.github.musicsnsproject.repository.jpa.account.role;

import com.github.musicsnsproject.common.converter.custom.RoleConverter;
import com.github.musicsnsproject.common.myenum.RolesEnum;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "roles")
@Getter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Convert(converter = RoleConverter.class)
    private RolesEnum name;

    public static Role onlyId(RolesEnum rolesEnum) {
        Role role = new Role();
        role.roleId = switch (rolesEnum){
            case ROLE_USER -> 22L;
            case ROLE_ADMIN -> 21L;
            case ROLE_SUPER_USER -> 23L;
        };
        return role;
    }
}