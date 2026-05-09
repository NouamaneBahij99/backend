package com.magentatechno.pelican.repository;

import com.magentatechno.pelican.entity.Role;
import com.magentatechno.pelican.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByEnabledTrue(Pageable pageable);

    Optional<User> findFirstByRoleAndEnabledTrue(Role role);
}
