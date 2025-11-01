package com.tfm.bandas.users.model.repository;


import com.tfm.bandas.users.model.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserProfileEntity, Long>,
        JpaSpecificationExecutor<UserProfileEntity> {

    Optional<UserProfileEntity> findByEmail(String email);
    Optional<UserProfileEntity> findByIamId(String iamId);
    Optional<UserProfileEntity> findByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByIamId(String iamId);
}