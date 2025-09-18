package com.github.musicsnsproject.repository.jpa.account.role;

import com.github.musicsnsproject.common.converter.custom.RoleConverter;
import com.github.musicsnsproject.common.myenum.RoleEnum;
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
    private RoleEnum name;

    public static Role fromName(RoleEnum roleEnum) {
        Role role = new Role();
        role.roleId = switch (roleEnum){
            case ROLE_USER -> 6L;
            case ROLE_ADMIN -> 4L;
            case ROLE_SUPER_USER -> 5L;
        };
        return role;
    }
}