package com.restaurant.hnks24cntt1it210tranhuunhatproject.repository;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {

    boolean existsByEmail(String email);

    boolean existsByStudentIdCode(String studentIdCode);
}