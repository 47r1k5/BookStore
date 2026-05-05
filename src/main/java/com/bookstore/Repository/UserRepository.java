package com.bookstore.Repository;

import com.bookstore.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    Optional<UserEntity> findByUsernameIgnoreCase(String username);

    Optional<UserEntity> findByEmailIgnoreCase(String email);
}