package com.dguossp.santong.repository;

import com.dguossp.santong.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Roles, Long> {

    Roles findById(long id);

}
