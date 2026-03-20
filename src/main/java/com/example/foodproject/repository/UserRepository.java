package com.example.foodproject.repository;

import com.example.foodproject.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findById(Long aLong);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByDisplayName(String displayName);

    Iterable<User> findAll();
}
