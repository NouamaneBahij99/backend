package com.magentatechno.pelican.repository;

import com.magentatechno.pelican.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role = 'AGENT' OR u.role = 'CHEF_SERVICE' OR u.role = 'DIRECTEUR'")
    java.util.List<User> findAllActiveUsers();
}
