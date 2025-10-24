package com.tfm.bandas.usuarios.model.repository;


import com.tfm.bandas.usuarios.model.entity.UserProfileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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