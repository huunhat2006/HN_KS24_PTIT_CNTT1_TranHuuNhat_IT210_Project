package com.restaurant.hnks24cntt1it210tranhuunhatproject.repository;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userProfile")
    List<User> findAllWithProfile();
}