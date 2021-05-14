package com.dguossp.santong.entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
public class Roles {

    // 식별자 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 권한 명 (예 - ROLE_ADMIN, ROLE_USER ...)
    private String name;

    @ManyToMany(mappedBy = "rolesSet")
    private Set<Users> usersSet = new HashSet<>();

}
