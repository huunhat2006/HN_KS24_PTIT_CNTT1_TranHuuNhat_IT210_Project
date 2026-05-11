package com.restaurant.hnks24cntt1it210tranhuunhatproject.service;

import com.restaurant.hnks24cntt1it210tranhuunhatproject.dto.UserProfileDTO;

public interface UserProfileService {
    UserProfileDTO getProfileDTOByUserId(Integer userId);
    UserProfileDTO updateProfile(Integer userId, UserProfileDTO profileDTO);
}