package com.restaurant.hnks24cntt1it210tranhuunhatproject.service.impl;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.UserProfileDTO;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.entity.UserProfile;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.repository.UserProfileRepository;
import com.restaurant.hnks24cntt1it210tranhuunhatproject.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getProfileDTOByUserId(Integer userId) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Hồ sơ không tìm thấy"));

        return UserProfileDTO.builder()
                .userId(userProfile.getUserId())
                .fullName(userProfile.getFullName())
                .email(userProfile.getEmail())
                .phone(userProfile.getPhone())
                .studentIdCode(userProfile.getStudentIdCode())
                .build();
    }

    @Override
    @Transactional
    public UserProfileDTO updateProfile(Integer userId, UserProfileDTO profileDTO) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Hồ sơ không tìm thấy"));

        if (profileDTO.getFullName() != null && !profileDTO.getFullName().isBlank()) {
            userProfile.setFullName(profileDTO.getFullName());
        }

        if (profileDTO.getPhone() != null) {
            userProfile.setPhone(profileDTO.getPhone());
        }

        userProfileRepository.save(userProfile);

        return UserProfileDTO.builder()
                .userId(userProfile.getUserId())
                .fullName(userProfile.getFullName())
                .email(userProfile.getEmail())
                .phone(userProfile.getPhone())
                .studentIdCode(userProfile.getStudentIdCode())
                .build();
    }
}